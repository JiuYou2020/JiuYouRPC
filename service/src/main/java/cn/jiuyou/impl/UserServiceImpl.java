package cn.jiuyou.impl;


import cn.jiuyou.UserService;
import cn.jiuyou.entity.User;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/24 21:27
 * {@code @Description: } UserService实现类
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(String id) {
        User user = new User();
        user.setId(id);
        user.setAge(18);
        user.setUsername("jiuyou2020");
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = new User();
        user.setId("123");
        user.setAge(18);
        user.setUsername(username);
        return user;
    }
}
