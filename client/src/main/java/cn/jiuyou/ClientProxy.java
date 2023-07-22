package cn.jiuyou;


import cn.jiuyou.entity.RpcRequest;
import cn.jiuyou.entity.RpcResponse;
import cn.jiuyou.netty.NettyClient;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 00:32
 * {@code @Description: } 使用jdk动态代理，实现客户端的远程调用
 */
@AllArgsConstructor
@SuppressWarnings("all")
public class ClientProxy implements InvocationHandler {
    private Client client;

    public static <T> T getProxy(Class<T> serviceInterface, Client client) {
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, new ClientProxy(client));
    }

    /**
     * 默认使用NettyClient
     */
    public static <T> T getProxy(Class<T> serviceInterface) {
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, new ClientProxy(new NettyClient()));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        //封装RpcRequest对象
        RpcRequest rpcRequest = RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .build();
        //发起远程调用
        RpcResponse rpcResponse = client.call(rpcRequest);
        Object data = rpcResponse.getData();
        return data;
    }
}
