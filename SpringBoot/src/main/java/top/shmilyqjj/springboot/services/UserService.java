package top.shmilyqjj.springboot.services;

import top.shmilyqjj.springboot.models.entity.User;

import java.util.List;

/**
 * @author Shmily
 * @Description:
 * @CreateTime: 2024/1/3 下午4:41
 * @Site: shmily-qjj.top
 */

public interface UserService {
    List<User> getUserList();
    User getUserByName(String name);

    List<String> streamListUsers();
}
