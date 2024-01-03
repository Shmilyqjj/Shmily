package top.shmilyqjj.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.shmilyqjj.springboot.models.entity.User;
import top.shmilyqjj.springboot.services.JsonService;
import top.shmilyqjj.springboot.services.UserService;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author Shmily
 * @Description:
 * @CreateTime: 2024/1/3 下午4:49
 * @Site: shmily-qjj.top
 */

@RestController
@Tag(name = "用户模块", description = "用户信息相关接口")
public class UserController {
    @Autowired
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(HttpController.class);

    @ResponseBody
    @GetMapping("/user/list")
    @Operation(summary = "获取用户列表", description = "获取用户列表，返回Json")
    public List<User> listUser() {
        return userService.getUserList();
    }

    @ResponseBody
    @GetMapping("/user/get")
    @Operation(summary = "获取用户信息", description = "根据用户名获取用户信息")
    public User getUser(@RequestParam("name")@Parameter(name = "name",description = "名字", example = "QJJ") String name) {
        return userService.getUserByName(name);
    }

    @ResponseBody
    @GetMapping("/user/list/stream")
    @Operation(summary = "获取用户列表", description = "获取用户列表，返回Json")
    public List<String> streamListUser() {
        return userService.streamListUsers();
    }

}
