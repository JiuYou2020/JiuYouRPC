package cn.jiuyou;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 00:50
 * {@code @Description: }
 */
public interface AccountService {
    /**
     * 根据userId获取用户的余额
     *
     * @param id 用户id
     * @return 余额
     */
    double getMoneyById(String id);
}
