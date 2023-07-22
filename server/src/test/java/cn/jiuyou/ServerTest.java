package cn.jiuyou;

import cn.jiuyou.impl.AccountServiceImpl;
import cn.jiuyou.impl.UserServiceImpl;
import cn.jiuyou.netty.NettyServer;
import cn.jiuyou.serializer.SerializerManager;
import cn.jiuyou.serializer.impl.KryoSerializer;
import org.junit.Test;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/15
 * {@code @Description: }
 */
public class ServerTest {
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
}
