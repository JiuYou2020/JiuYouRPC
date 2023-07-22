package cn.jiuyou.netty;


import cn.jiuyou.ServiceProvider;
import cn.jiuyou.constant.Payload;
import cn.jiuyou.entity.RpcRequest;
import cn.jiuyou.entity.RpcResponse;
import cn.jiuyou.serviceDiscovery.impl.ZookeeperServiceDiscovery;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.x.discovery.ServiceInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 13:51
 * {@code @Description: } 因为是服务器端，我们知道接受到请求格式是RPCRequest
 */
@AllArgsConstructor
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private ServiceProvider serviceProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = getResponse(rpcRequest);
        ctx.writeAndFlush(response);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    RpcResponse getResponse(RpcRequest request) throws Exception {
        //获取Discovery
        ZookeeperServiceDiscovery discovery = ZookeeperServiceDiscovery.getInstance();
        // 得到服务名
        String interfaceName = request.getInterfaceName();
        //增加连接数
        ServiceInstance<Payload> serviceInstance = serviceProvider.getServiceInstance(interfaceName);
        serviceInstance.getPayload().incrementActiveCount();
        discovery.updateService(serviceInstance);
        log.info("服务实例:{}  活跃数+1 ，当前活跃数:{}", serviceInstance, serviceInstance.getPayload().getActiveCount());
        // 得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        // 反射调用方法
        Method method;
        try {
            method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            Object invoke = method.invoke(service, request.getParams());
            return RpcResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return RpcResponse.error("方法执行错误");
        } finally {
            //减少连接数
            serviceInstance.getPayload().decrementActiveCount();
            discovery.updateService(serviceInstance);
            log.info("服务实例:{}  活跃数-1 ，当前活跃数:{}", serviceInstance, serviceInstance.getPayload().getActiveCount());
        }
    }
}
