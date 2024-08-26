package top.shmilyqjj.springboot.controller;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/6/18 10:18
 * @Site: shmily-qjj.top
 */
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import top.shmilyqjj.springboot.models.request.DemoReq;
import top.shmilyqjj.springboot.models.response.DemoRes;
import top.shmilyqjj.springboot.services.HttpExchangeService;
import top.shmilyqjj.springboot.services.JsonService;
import top.shmilyqjj.springboot.services.api.LocalHostWebClient1Api;
import top.shmilyqjj.springboot.services.api.LocalHostWebClient2Api;


@RestController
@Tag(name = "Http模块", description = "Http请求接口")
public class HttpController {

    @Autowired
    JsonService jsonService;

    @Resource
    HttpExchangeService httpExchangeService;

    @Resource
    LocalHostWebClient1Api localHostWebClient1Api;

    @Resource
    LocalHostWebClient2Api localHostWebClient2Api;

    private static final Logger logger = LoggerFactory.getLogger(HttpController.class);

    @ResponseBody
    @GetMapping("/hello/{name}")
    @Operation(summary = "/hello/{name}方法", description = "动态入参")
    public String hello(@PathVariable("name")@Parameter(description = "名字",example="qjj") String name) throws UnsupportedEncodingException {
        // curl "http://localhost:8082/hello/qjj"
        logger.debug("[Get]Hello " + name);
        return "Hello " + name;
    }

    @ResponseBody
    @GetMapping("/hello")
    @Operation(summary = "/hello方法", description = "GET方法，入参name，age")
    @Parameters({
            @Parameter(name = "name",description = "名字", example = "qjj"),
            @Parameter(name = "age",description = "年龄",example = "26")
    })
    public String hello(@RequestParam("name") String name, @RequestParam(name = "age", required = false) String age) throws UnsupportedEncodingException {
        // curl "http://localhost:8082/hello?name=qjj&&age=24"
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
    @Operation(summary = "/hello方法", description = "POST方法，入参json 使用RequestBody解析成Map")
    @Parameter(description = "json入参",example="{\"a\": \"b\", \"c\":\"d\"}")
    public String hello(@RequestBody Map<String, Object> params) throws UnsupportedEncodingException {
        // curl -H "Content-Type: application/json;charset=utf-8" -X POST "http://localhost:8082/hello" -d '{"name": "qjj", "age": "24"}'
        String name = String.valueOf(params.get("name"));
        int age = Integer.parseInt(String.valueOf(params.get("age")));
        logger.debug("[POST]Hello"  + name + age);
        return "Hello " + name + age;
    }

    //前端请求传Json对象则后端使用@RequestParam
    //前端请求传Json对象的字符串则后端使用@RequestBody
    @ResponseBody
    @PostMapping("/form")
    @Operation(summary = "/form", description = "入参form")
    public String form(@RequestParam Map<String, String> params) throws UnsupportedEncodingException {
        //  curl -H "Content-Type: application/x-www-form-urlencoded" -X POST "http://localhost:8082/form" -d "name=qjj&age=24"
        String name = String.valueOf(params.get("name"));
        int age = Integer.parseInt(String.valueOf(params.get("age")));
        logger.debug("[POST]Hello"  + name + age);
        return "Hello " + name + age;
    }

    @ResponseBody
    @PostMapping(value = "/json", produces = "application/json;charset=utf-8")
    @Operation(summary = "/json方法", description = "入参json")
    public String json(@RequestBody JsonObject jsonObject) {
        // curl -H "Content-Type: application/json;charset=utf-8" -X POST "http://localhost:8082/json" -d '{"name": "qjj", "age": "24", "version": "2.0"}'
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
    @Operation(summary = "/demo方法", description = "POST方法，入参DemoReq类的属性json")
    public String demoPost(@RequestBody DemoReq req) {
        // curl -H "Content-Type: application/json;charset=utf-8" -X POST "http://localhost:8082/demo" -d '{"name": "qjj", "age": "24"}'
        return "Hello "+ req.getName() + " age: "+ req.getAge();
    }

    @ResponseBody
    @PutMapping(value = "/put")
    @Operation(summary = "/put方法", description = "PUT方法，入参DemoReq类的属性json")
    @ApiResponse(responseCode = "200", description = "返回DemoRes正常",content = {@Content(array= @ArraySchema(schema = @Schema(implementation = DemoRes.class)))})
    @ApiResponse(responseCode = "500", description = "返回DemoRes错误")
    public DemoRes demoPut(@RequestBody DemoReq req) {
        // curl -H "Content-Type: application/json;charset=utf-8" -X POST "http://localhost:8082/demo" -d '{"name": "qjj", "age": "24"}'
        return new DemoRes(req.getName(), req.getAge());
    }

    @ResponseBody
    @PostMapping("/file/upload")
    @Operation(summary = "/file/upload方法", description = "POST方法，文件上传")
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

    @GetMapping(value = "/exchange/get")
    @Operation(summary = "/exchange/get方法", description = "调用HttpExchange get方法")
    public String httpExchangeGet(){
        String c1 = localHostWebClient1Api.invokeHttpHello("localHostWebClient1");
        String c2 = localHostWebClient2Api.invokeHttpHello("localHostWebClient2");
        String s = httpExchangeService.invokeHttpHello("invokeHttpHello");
        return s + "," + c1 + "," + "," +c2;
    }


    @ResponseBody
    @PostMapping(value = "/exchange/post", produces = "application/json;charset=utf-8")
    @Operation(summary = "/exchange/post方法", description = "调用HttpExchange post方法")
    public String httpExchangePost(@RequestBody Map<String, Object> params){
        params.put("/exchange/post", "invokeHttpJson");
        return httpExchangeService.invokeHttpJson(params);
    }

}