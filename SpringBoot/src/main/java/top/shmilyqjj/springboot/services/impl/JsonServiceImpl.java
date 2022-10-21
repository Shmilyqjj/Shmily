package top.shmilyqjj.springboot.services.impl;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;
import top.shmilyqjj.springboot.services.JsonService;

@Component  // 加上@Component注解的类会自动被Spring扫描到生成Bean注册到spring容器中
public class JsonServiceImpl implements JsonService {
    @Override
    public String getJsonString(JsonObject jsonObject) {
        return jsonObject.toString();
    }
}
