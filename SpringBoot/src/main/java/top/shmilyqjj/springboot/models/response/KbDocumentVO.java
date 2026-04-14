package top.shmilyqjj.springboot.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "知识库文档信息")
public class KbDocumentVO {
    private Long id;
    private String originalFilename;
    private String contentType;
    private LocalDateTime createdAt;
}
