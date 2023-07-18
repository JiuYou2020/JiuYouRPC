package cn.jiuyou;


import cn.jiuyou.entity.RpcRequest;
import cn.jiuyou.entity.RpcResponse;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 13:10
 * {@code @Description: } Client抽象接口
 */
public interface Client {
    /**
     * 发起远程调用
     *
     * @param rpcRequest rpc请求
     * @return rpc响应
     * @throws Exception 异常
     */
    RpcResponse call(RpcRequest rpcRequest) throws Exception;
}
