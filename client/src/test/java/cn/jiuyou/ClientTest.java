package cn.jiuyou;

import cn.jiuyou.impl.netty.NettyClient;
import cn.jiuyou.serializer.SerializerManager;
import cn.jiuyou.serializer.impl.KryoSerializer;
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
        SerializerManager.setCompressionImpl(new KryoSerializer());
        UserService userServiceProxy = ClientProxy.getProxy(UserService.class, new NettyClient());
        Object user = userServiceProxy.getUserById("123");
        System.out.println(user);
        AccountService accountServiceProxy = ClientProxy.getProxy(AccountService.class);
        double money = accountServiceProxy.getMoneyById("123");
        System.out.println(money);
    }
}