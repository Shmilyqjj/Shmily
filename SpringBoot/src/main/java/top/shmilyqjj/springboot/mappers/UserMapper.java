package top.shmilyqjj.springboot.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.cursor.Cursor;
import top.shmilyqjj.springboot.models.entity.User;

import java.util.List;

/**
 * @author Shmily
 * @Description:
 * @CreateTime: 2024/1/3 下午4:59
 * @Site: shmily-qjj.top
 */

@Mapper
public interface UserMapper {
    List<User> listUsers();

    User getUserByName(String userName);
    Cursor<String> streamListUsers();
}
