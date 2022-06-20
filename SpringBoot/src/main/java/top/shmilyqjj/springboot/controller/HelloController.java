package top.shmilyqjj.springboot.controller;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/6/18 10:18
 * @Site: shmily-qjj.top
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class HelloController {
    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        // http://localhost:8080/hello
        return "Hello World!";
    }
}