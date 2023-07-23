package cn.jiuyou;

import cn.jiuyou.http2.JiuYouHttp2Client;
import cn.jiuyou.http2.JiuYouHttp2Server;
import cn.jiuyou.impl.AccountServiceImpl;
import cn.jiuyou.impl.UserServiceImpl;
import cn.jiuyou.netty.NettyClient;
import cn.jiuyou.netty.NettyServer;
import cn.jiuyou.serializer.SerializerManager;
import cn.jiuyou.serializer.impl.KryoSerializer;
import cn.jiuyou.serviceDiscovery.impl.ZookeeperServiceDiscovery;
import cn.jiuyou.serviceDiscovery.strategies.Impl.WeightedRoundRobinStrategy;
import org.junit.Test;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/15
 * {@code @Description: }
 */
public class ClientTest {
    @Test
    public void testClient() {
//        CompressionManager.setCompressionImpl(new GzipCompression());
        ZookeeperServiceDiscovery.setProviderStrategy(new WeightedRoundRobinStrategy());
        SerializerManager.setCompressionImpl(new KryoSerializer());
        UserService userServiceProxy = ClientProxy.getProxy(UserService.class, new NettyClient());
        Object user = userServiceProxy.getUserById("123");
        System.out.println(user);
    }

    @Test
    public void testServer() throws Exception {
//        CompressionManager.setCompressionImpl(new GzipCompression());
        SerializerManager.setCompressionImpl(new KryoSerializer());
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.addService(new UserServiceImpl(), 500);
        serviceProvider.addService(new UserServiceImpl(), 100);
        serviceProvider.addService(new AccountServiceImpl());
        Server server = new NettyServer(serviceProvider);
        server.run();
    }

    @Test
    public void testHttp2Client() throws Exception {
        ZookeeperServiceDiscovery.setProviderStrategy(new WeightedRoundRobinStrategy());
        UserService userServiceProxy = ClientProxy.getProxy(UserService.class, new JiuYouHttp2Client());
        Object user = userServiceProxy.getUserById("123");
        System.out.println(user);
    }

    @Test
    public void testHttp2Server() throws Exception {
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.addService(new UserServiceImpl(), 500);
        serviceProvider.addService(new UserServiceImpl(), 100);
        serviceProvider.addService(new AccountServiceImpl());
        Server server = new JiuYouHttp2Server();
        server.run();
    }

}
