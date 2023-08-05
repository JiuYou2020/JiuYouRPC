package cn.jiuyou;

import cn.jiuyou.http2.JiuYouHttp2Client;
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
    public void testHttp2Client() throws Exception {
        ZookeeperServiceDiscovery.setProviderStrategy(new WeightedRoundRobinStrategy());
        UserService userServiceProxy = ClientProxy.getProxy(UserService.class, new JiuYouHttp2Client());
        Object user = userServiceProxy.getUserById("123");
        System.out.println(user);
    }
}
