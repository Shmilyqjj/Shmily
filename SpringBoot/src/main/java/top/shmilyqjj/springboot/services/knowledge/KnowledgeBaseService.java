package top.shmilyqjj.springboot.services.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.shmilyqjj.springboot.config.KnowledgeBaseProperties;
import top.shmilyqjj.springboot.exception.ApiErrorCode;
import top.shmilyqjj.springboot.exception.ApiException;
import top.shmilyqjj.springboot.mappers.KbDocumentMapper;
import top.shmilyqjj.springboot.models.entity.KbDocument;
import top.shmilyqjj.springboot.models.response.KbDocumentVO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import redis.clients.jedis.JedisPooled;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private static final Set<String> ALLOWED_EXT = Set.of(".txt", ".md", ".json", ".markdown");

    private final KbDocumentMapper kbDocumentMapper;
    private final VectorStore vectorStore;
    private final KnowledgeBaseProperties properties;
    private final ObjectProvider<RedisVectorStore> redisVectorStore;
    private final TokenTextSplitter textSplitter = new TokenTextSplitter();

    @Value("${spring.ai.vectorstore.redis.index-name:kb-vector-index}")
    private String redisVectorIndexName;

    private final Object indexLock = new Object();

    /**
     * 解析为绝对路径，避免嵌入式 Tomcat 将 {@code ./xxx} 解析到 work 目录且与 multipart 写入不一致。
     */
    public Path filesDirectory() {
        Path root = Path.of(properties.getStorageDir()).toAbsolutePath().normalize();
        return root.resolve("files").normalize();
    }

    /**
     * 启动策略：
     * <ul>
     *   <li>{@code reindexFromDbOnStartup=true}：每次启动从 MySQL 全量重建 Redis 向量。</li>
     *   <li>否则：若库中有文档但 Redis 索引中无向量（常见于 Redis 无持久化、FLUSH、连错实例），则自动重建；否则不重跑向量化。</li>
     * </ul>
     */
    public void reconcileVectorStoreOnStartup() {
        if (properties.isReindexFromDbOnStartup()) {
            synchronized (indexLock) {
                log.info("Knowledge base: reindex-from-db-on-startup=true, rebuilding Redis vectors from DB");
                rebuildIndexFromDatabaseLocked();
            }
            return;
        }
        long dbCount = kbDocumentMapper.countAll();
        if (dbCount == 0) {
            log.info("Knowledge base: no rows in kb_document, skip Redis vector sync");
            return;
        }
        RedisVectorStore redis = redisVectorStore.getIfAvailable();
        if (redis == null) {
            log.info("Knowledge base: RedisVectorStore bean not available, skip empty-index check");
            return;
        }
        if (!isRedisKnowledgeIndexEmpty(redis)) {
            log.info("Knowledge base: Redis index '{}' has documents (num_docs>0), skip startup reindex", redisVectorIndexName);
            return;
        }
        synchronized (indexLock) {
            log.warn(
                    "Knowledge base: DB has {} kb_document row(s) but Redis index '{}' has no vectors "
                            + "(常见：Redis 仅内存未持久化、容器重启丢数据、执行过 FLUSHDB、或曾连到另一台 Redis)。正在从 MySQL 自动重建向量。",
                    dbCount, redisVectorIndexName);
            rebuildIndexFromDatabaseLocked();
        }
    }

    /**
     * 通过 FT.INFO 判断当前索引是否无文档；索引不存在或查询失败时视为“空”，以便从 DB 回填。
     */
    private boolean isRedisKnowledgeIndexEmpty(RedisVectorStore store) {
        return store.getNativeClient().map(client -> {
            if (!(client instanceof JedisPooled jedis)) {
                return false;
            }
            try {
                Map<String, Object> info = jedis.ftInfo(redisVectorIndexName);
                return parseNumDocs(info) == 0L;
            } catch (Exception e) {
                String m = e.getMessage() != null ? e.getMessage() : "";
                if (m.toLowerCase().contains("unknown") && m.toLowerCase().contains("index")) {
                    return true;
                }
                log.warn("Knowledge base: ftInfo('{}') failed, assume index empty: {}", redisVectorIndexName, m);
                return true;
            }
        }).orElse(false);
    }

    private static long parseNumDocs(Map<String, Object> info) {
        if (info == null || info.isEmpty()) {
            return 0L;
        }
        Object n = info.get("num_docs");
        if (n == null) {
            return 0L;
        }
        if (n instanceof Long) {
            return (Long) n;
        }
        if (n instanceof Integer) {
            return ((Integer) n).longValue();
        }
        try {
            return Long.parseLong(n.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * 从数据库全量写入 Redis 向量（调用方一般无需直接调用；会持有索引锁）。
     */
    public void rebuildIndexFromDatabase() {
        synchronized (indexLock) {
            rebuildIndexFromDatabaseLocked();
        }
    }

    private void rebuildIndexFromDatabaseLocked() {
        List<KbDocument> all = kbDocumentMapper.listAll();
        log.info("Knowledge base: rebuilding Redis vector index, document count={}", all.size());
        for (KbDocument doc : all) {
            try {
                // 全量重建时先删该文档旧向量，避免与 Redis 中残留片段重复
                indexDocumentInternal(doc.getId(), true);
            } catch (Exception e) {
                log.warn("Skip indexing kb_document id={}: {}", doc.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public KbDocumentVO upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ApiException(ApiErrorCode.KB_FILE_EMPTY);
        }
        String original = file.getOriginalFilename();
        if (!StringUtils.hasText(original)) {
            throw new ApiException(ApiErrorCode.KB_MISSING_ORIGINAL_FILENAME);
        }
        String lower = original.toLowerCase(Locale.ROOT);
        String ext = extension(lower);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new ApiException(ApiErrorCode.KB_UNSUPPORTED_FILE_TYPE,
                    "仅支持扩展名: " + ALLOWED_EXT);
        }

        String stored = UUID.randomUUID() + ext;
        Path dir = filesDirectory();
        Files.createDirectories(dir);
        Path target = dir.resolve(stored).normalize();
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        KbDocument row = new KbDocument();
        row.setOriginalFilename(original);
        row.setStoredFilename(stored);
        row.setContentType(file.getContentType() != null ? file.getContentType() : "text/plain");
        kbDocumentMapper.insert(row);

        synchronized (indexLock) {
            indexDocumentInternal(row.getId(), false);
        }
        return toVo(row);
    }

    @Transactional
    public void deleteDocument(Long id) throws IOException {
        KbDocument doc = kbDocumentMapper.findById(id);
        if (doc == null) {
            throw new ApiException(ApiErrorCode.KB_DOCUMENT_NOT_FOUND);
        }
        synchronized (indexLock) {
            vectorStore.delete("kbDocId == '" + id + "'");
            Path path = filesDirectory().resolve(doc.getStoredFilename());
            Files.deleteIfExists(path);
            kbDocumentMapper.deleteById(id);
        }
    }

    public List<KbDocumentVO> listDocuments() {
        return kbDocumentMapper.listAll().stream().map(this::toVo).collect(Collectors.toList());
    }

    private void indexDocumentInternal(Long id, boolean deleteOldChunksFirst) throws IOException {
        KbDocument doc = kbDocumentMapper.findById(id);
        if (doc == null) {
            return;
        }
        Path path = filesDirectory().resolve(doc.getStoredFilename());
        if (!Files.isRegularFile(path)) {
            log.warn("Knowledge file missing for id={}, path={}", id, path);
            return;
        }
        String text = Files.readString(path, StandardCharsets.UTF_8);
        if (!StringUtils.hasText(text)) {
            log.warn("Empty file skipped id={}", id);
            return;
        }
        if (deleteOldChunksFirst) {
            vectorStore.delete("kbDocId == '" + id + "'");
        }
        Map<String, Object> meta = new HashMap<>();
        meta.put("kbDocId", String.valueOf(id));
        meta.put("originalFilename", doc.getOriginalFilename());
        Document root = new Document(text, meta);
        List<Document> chunks = textSplitter.split(root);
        vectorStore.add(chunks);
        log.info("Indexed kb_document id={}, chunks={}", id, chunks.size());
    }

    private KbDocumentVO toVo(KbDocument d) {
        return KbDocumentVO.builder()
                .id(d.getId())
                .originalFilename(d.getOriginalFilename())
                .contentType(d.getContentType())
                .createdAt(d.getCreatedAt())
                .build();
    }

    private static String extension(String lowerFilename) {
        int i = lowerFilename.lastIndexOf('.');
        if (i < 0) {
            return "";
        }
        return lowerFilename.substring(i);
    }
}
