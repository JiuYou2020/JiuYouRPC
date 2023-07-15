package cn.jiuyou.impl;


import cn.jiuyou.AccountService;

import java.util.Random;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 00:51
 * {@code @Description: } AccountService实现类
 */
public class AccountServiceImpl implements AccountService {
    @Override
    public double getMoneyById(String id) {
        return new Random().nextDouble() * 1000.0;
    }
}
