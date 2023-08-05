package cn.jiuyou.utils;

import cn.hutool.core.lang.Snowflake;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/8/5
 * {@code @Description: }
 */
public class IdGenerator {
    private static final Snowflake SNOWFLAKE = new Snowflake(1, 1);

    public static long generateId() {
        return SNOWFLAKE.nextId();
    }
}
