package cn.jiuyou;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 01:39
 * {@code @Description: } RpcServer的抽象接口
 */
public interface Server {
    /**
     * 开启服务
     */
    void run() throws Exception;

    /**
     * 停止服务
     */
    void stop();
}
