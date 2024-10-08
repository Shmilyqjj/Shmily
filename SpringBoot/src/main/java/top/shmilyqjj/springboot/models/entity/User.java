package top.shmilyqjj.springboot.models.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Shmily
 * @Description: User实体类
 * @CreateTime: 2024/1/3 下午4:48
 * @Site: shmily-qjj.top
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User实体类")
public class User {
    @Schema(description = "用户ID", example = "123")
    String id;

    @Schema(description = "用户名", example = "shmily")
    String name;

    @Schema(description = "年龄", example = "26")
    Integer age;
}
