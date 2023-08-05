package cn.jiuyou;

import cn.jiuyou.http2.JiuYouHttp2Server;
import cn.jiuyou.impl.AccountServiceImpl;
import cn.jiuyou.impl.UserServiceImpl;
import org.junit.Test;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/15
 * {@code @Description: }
 */
public class ServerTest {

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
