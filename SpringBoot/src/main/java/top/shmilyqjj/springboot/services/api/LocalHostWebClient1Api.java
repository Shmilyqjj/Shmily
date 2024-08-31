package top.shmilyqjj.springboot.services.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * @author Shmily
 * @Description: 多WebClient支持
 * @CreateTime: 2024/8/26 下午8:32
 */

public interface LocalHostWebClient1Api {
    @GetExchange("/hello/{name}")
    String invokeHttpHello(@PathVariable("name") String name);
}