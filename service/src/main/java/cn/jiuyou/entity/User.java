package cn.jiuyou.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/24 21:24
 * {@code @Description: } User实体类
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private Integer age;
}
