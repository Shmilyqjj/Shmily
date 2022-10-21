package top.shmilyqjj.springboot.controller;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/6/18 10:18
 * @Site: shmily-qjj.top
 */
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import top.shmilyqjj.springboot.services.impl.JsonServiceImpl;


@Controller
public class HelloController {
    @Autowired
    JsonServiceImpl jsonService;

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

    //前端请求传Json对象则后端使用@RequestParam
    //前端请求传Json对象的字符串则后端使用@RequestBody
    @ResponseBody
    @PostMapping("/hello")
    public String hello(@RequestBody Map<String, Object> params) throws UnsupportedEncodingException {
        // curl -H "Content-Type: application/json;charset=utf-8" -X POST "http://localhost:8080/hello" -d '{"name": "qjj", "age": "24"}'
        String name = String.valueOf(params.get("name"));
        int age = Integer.parseInt(String.valueOf(params.get("age")));
        return "Hello " + name + age;
    }

    //前端请求传Json对象则后端使用@RequestParam
    //前端请求传Json对象的字符串则后端使用@RequestBody
    @ResponseBody
    @PostMapping("/form")
    public String form(@RequestParam Map<String, String> params) throws UnsupportedEncodingException {
        //  curl -H "Content-Type: application/x-www-form-urlencoded" -X POST "http://localhost:8080/form" -d "name=qjj&age=24"
        String name = String.valueOf(params.get("name"));
        int age = Integer.parseInt(String.valueOf(params.get("age")));
        return "Hello " + name + age;
    }

    @ResponseBody
    @PostMapping(value = "/json", produces = "application/json;charset=utf-8")
    public String json(@RequestBody JsonObject jsonObject) {
        // curl -H "Content-Type: application/json;charset=utf-8" -X POST "http://localhost:8080/json" -d '{"name": "qjj", "age": "24", "version": "2.0"}'
        String version = jsonObject.has("version") ? jsonObject.get("version").getAsString() : "1.0";
        return "version: "+ version + " data: "+ jsonService.getJsonString(jsonObject);
    }

}