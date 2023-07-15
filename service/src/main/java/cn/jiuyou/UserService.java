package cn.jiuyou;

import cn.jiuyou.entity.User;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/24 21:26
 * {@code @Description: } UserService接口
 */
public interface UserService {
    /**
     * 根据id查询用户
     *
     * @param id 用户id
     * @return User
     */
    User getUserById(String id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return User
     */
    User getUserByUsername(String username);
}
