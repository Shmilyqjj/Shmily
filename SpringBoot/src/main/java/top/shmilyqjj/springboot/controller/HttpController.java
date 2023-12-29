package top.shmilyqjj.springboot.controller;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/6/18 10:18
 * @Site: shmily-qjj.top
 */
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import top.shmilyqjj.springboot.models.DemoReq;
import top.shmilyqjj.springboot.services.JsonService;


@Controller
public class HttpController {
    @Autowired
    JsonService jsonService;

    private static final Logger logger = LoggerFactory.getLogger(HttpController.class);

    @ResponseBody
    @GetMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name) throws UnsupportedEncodingException {
        // curl "http://localhost:8080/hello/qjj"
        logger.debug("[Get]Hello " + name);
        return "Hello " + name;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(@RequestParam("name") String name, @RequestParam(name = "age", required = false) String age) throws UnsupportedEncodingException {
        // curl "http://localhost:8080/hello?name=qjj&&age=24"
        if(age == null){
            age = "*";
        }
        logger.debug("[Get]Hello " + name + age);
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
        logger.debug("[POST]Hello"  + name + age);
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
        logger.debug("[POST]Hello"  + name + age);
        return "Hello " + name + age;
    }

    @ResponseBody
    @PostMapping(value = "/json", produces = "application/json;charset=utf-8")
    public String json(@RequestBody JsonObject jsonObject) {
        // curl -H "Content-Type: application/json;charset=utf-8" -X POST "http://localhost:8080/json" -d '{"name": "qjj", "age": "24", "version": "2.0"}'
        String res;
        if (jsonObject.has("version")) {
            res = "version: "+ jsonObject.get("version").getAsString() + " #JSON_DATA: "+ jsonService.getJsonString(jsonObject);
        }else {
            res = "#JSON_DATA: "+ jsonService.getJsonString(jsonObject);
        }
        logger.debug(res);
        return res;
    }

    @ResponseBody
    @PostMapping(value = "/demo")
    public String demo(@RequestBody DemoReq req) {
        // curl -H "Content-Type: application/json;charset=utf-8" -X POST "http://localhost:8080/demo" -d '{"name": "qjj", "age": "24"}'
        return "Hello "+ req.getName() + " age: "+ req.getAge();
    }

    @ResponseBody
    @PostMapping("/file/upload")
    public String upload(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()){
            return "未选择文件";
        }
        //获取上传文件原来的名称
        String filename = file.getOriginalFilename();
        String filePath = "/tmp/";
        File temp = new File(filePath);
        if (!temp.exists()){
            temp.mkdirs();
        }
        File localFile = new File(filePath + filename);
        try {
            file.transferTo(localFile); //把上传的文件保存至本地
            logger.debug(file.getOriginalFilename()+" 成功上传到服务端文件: " + filePath + filename);
        }catch (IOException e){
            e.printStackTrace();
            return "上传失败" + e;
        }

        return "上传成功";

    }

}