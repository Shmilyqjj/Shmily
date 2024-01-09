package top.shmilyqjj.springboot.services.impl;


import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.shmilyqjj.springboot.mappers.UserMapper;
import top.shmilyqjj.springboot.models.entity.User;
import top.shmilyqjj.springboot.services.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shmily
 * @Description:
 * @CreateTime: 2024/1/3 下午4:42
 * @Site: shmily-qjj.top
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getUserList() {
        return userMapper.listUsers();
    }

    @Override
    public User getUserByName(String name) {
        return userMapper.getUserByName(name);
    }

    @Override
    @Transactional
    public List<String> streamListUsers() {
        List<String> usersNameList = new ArrayList<>();
        Cursor<String> cursor =userMapper.streamListUsers();
        //Cursor 实现了迭代器接口，因此在实际使用当中，从 Cursor 取数据非常简单
        cursor.forEach(usersNameList::add);
        System.out.println("返回多少条数据："+(cursor.getCurrentIndex()+1));
        System.out.println("取数据前判断Cursor是否为打开状态：" + cursor.isOpen() + "  判断是否已经取完全部结果：" + cursor.isConsumed());
        if(cursor.isConsumed()){
            return usersNameList;
        }
        return usersNameList;
    }
}
