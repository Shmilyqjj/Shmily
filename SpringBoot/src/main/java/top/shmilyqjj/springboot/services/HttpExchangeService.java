package top.shmilyqjj.springboot.services;

import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;

/**
 * @author Shmily
 * @Description: 调用外部http接口
 * @CreateTime: 2024/8/26 下午7:25
 * @Site: shmily-qjj.top
 */

@HttpExchange(url = "http://localhost:8082")
public interface HttpExchangeService {

    @HttpExchange(method = "GET", url = "/hello/{name}")
    String invokeHttpHello(@PathVariable("name") String name);

    @HttpExchange(method = "POST", url = "/json")
    String invokeHttpJson(@RequestBody Map<String, Object> params);
}
