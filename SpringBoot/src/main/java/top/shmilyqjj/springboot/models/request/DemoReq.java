package top.shmilyqjj.springboot.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
@Data
@ToString
@Schema(description = "DemoReq类")
public class DemoReq implements Serializable {

    @Schema(description = "姓名", example = "qjj")
    private String name;

    @Schema(description = "年龄", example = "26")
    private String age;
}
