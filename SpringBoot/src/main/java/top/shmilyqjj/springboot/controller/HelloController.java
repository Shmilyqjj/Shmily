package top.shmilyqjj.springboot.controller;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/6/18 10:18
 * @Site: shmily-qjj.top
 */
import com.google.gson.JsonObject;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;

@Controller
public class HelloController {
    @ResponseBody
    @GetMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name) throws UnsupportedEncodingException {
        // curl "http://localhost:8080/hello/qjj"
        return "Hello " + name;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(@RequestParam("name") String name, @RequestParam(name = "age", required = false) String age) throws UnsupportedEncodingException {
        // curl "http://localhost:8080/hello?name=qjj&&age=24"
        if(age == null){
            age = "*";
        }
        return "Hello " + name + age;
    }

    @ResponseBody
    @PostMapping("/hello")
    public String hello(@RequestBody Map<String, Object> params) throws UnsupportedEncodingException {
        //  curl -H "Content-Type: application/x-www-form-urlencoded" -X POST "http://localhost:8080/hello" -d "name=qjj&age=24"
        // curl -H "Content-Type: application/json;charset=utf-8" -X POST "http://localhost:8080/hello" -d '{"name": "qjj", "age": "24"}'
        String name = String.valueOf(params.get("name"));
        int age = Integer.parseInt(String.valueOf(params.get("age")));
        return "Hello " + name + age;
    }


}