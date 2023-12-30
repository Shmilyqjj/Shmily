package top.shmilyqjj.springboot.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@Schema(description = "DemoRes类")
public class DemoRes implements Serializable {

    @Schema(description = "姓名", example = "qjj")
    private String name;

    @Schema(description = "年龄", example = "26")
    private String age;
}
