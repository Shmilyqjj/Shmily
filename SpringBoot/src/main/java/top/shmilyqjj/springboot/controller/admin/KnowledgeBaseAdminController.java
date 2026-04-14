package top.shmilyqjj.springboot.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.shmilyqjj.springboot.models.response.KbDocumentVO;
import top.shmilyqjj.springboot.services.knowledge.KnowledgeBaseService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/kb")
@RequiredArgsConstructor
@Tag(name = "知识库管理", description = "上传、列出、删除知识库文档（触发向量索引更新）")
public class KnowledgeBaseAdminController {

    private final KnowledgeBaseService knowledgeBaseService;

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传知识库文档", description = "支持 UTF-8 文本：.txt / .md / .json / .markdown")
    public KbDocumentVO upload(@RequestPart("file") MultipartFile file) throws IOException {
        return knowledgeBaseService.upload(file);
    }

    @GetMapping("/documents")
    @Operation(summary = "文档列表")
    public List<KbDocumentVO> list() {
        return knowledgeBaseService.listDocuments();
    }

    @DeleteMapping("/documents/{id}")
    @Operation(summary = "删除文档", description = "删除记录、磁盘文件及向量库中对应片段")
    public void delete(@PathVariable Long id) throws IOException {
        knowledgeBaseService.deleteDocument(id);
    }
}
