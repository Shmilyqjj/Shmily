package top.shmilyqjj.springboot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.knowledge-base")
public class KnowledgeBaseProperties {

    /**
     * 知识库原文存储根目录（建议使用绝对路径或 ${java.io.tmpdir}，避免相对路径落在 Tomcat work 目录下不可写）
     */
    private String storageDir = System.getProperty("java.io.tmpdir") + "/shmily-knowledge-base";

    /**
     * 为 true 时：每次启动都从 MySQL 全量重建 Redis 向量（最稳但慢、耗 Embedding）。
     * 为 false（默认）时：若检测到库中有文档但 Redis 索引无向量，仍会「自动」重建一次；否则不重跑。
     */
    private boolean reindexFromDbOnStartup = false;
}
