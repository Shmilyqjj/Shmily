package top.shmilyqjj.springboot.models.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KbDocument {
    private Long id;
    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private LocalDateTime createdAt;
}
