package cn.jiuyou;


import cn.jiuyou.entity.RpcResponse;
import cn.jiuyou.http2.Http2ClientInitializer;
import cn.jiuyou.http2.Http2ClientResponseHandler;
import cn.jiuyou.http2.Http2SettingsHandler;
import cn.jiuyou.http2.Http2Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.ssl.SslContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static cn.jiuyou.constant.Constants.HOST;
import static cn.jiuyou.constant.Constants.PORT;
import static org.junit.Assert.assertEquals;

//Ensure the server class - Http2Server.java is already started before running this test
public class Http2ClientLiveTest {

    private static final Logger logger = LoggerFactory.getLogger(Http2ClientLiveTest.class);
    private SslContext sslCtx;
    private Channel channel;

    @Before
    public void setup() throws Exception {
        sslCtx = Http2Util.createSSLContext(false);
    }

    @Test
    public void whenRequestSent_thenHelloWorldReceived() throws Exception {
        // 创建工作线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 创建HTTP/2客户端的初始化器，指定SSL上下文、最大内容长度、服务器主机和端口
        Http2ClientInitializer initializer = new Http2ClientInitializer(sslCtx, Integer.MAX_VALUE, HOST, PORT);

        try {
            // 创建启动器
            Bootstrap b = new Bootstrap();
            // 配置启动器参数
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.remoteAddress(HOST, PORT);
            b.handler(initializer);

            // 连接到指定的HTTP/2服务器
            channel = b.connect()
                    .syncUninterruptibly()
                    .channel();

            logger.info("Connected to [" + HOST + ':' + PORT + ']');

            // 获取HTTP/2的设置处理器，并等待设置完成
            Http2SettingsHandler http2SettingsHandler = initializer.getSettingsHandler();
            http2SettingsHandler.awaitSettings(60, TimeUnit.SECONDS);

            logger.info("Sending request(s)...");

            // 创建一个HTTP/2 GET请求
            FullHttpRequest request = Http2Util.createGetRequest(HOST, PORT);

            // 获取HTTP/2的响应处理器
            Http2ClientResponseHandler responseHandler = initializer.getResponseHandler();
            int streamId = 3;

            // 将请求发送到服务器，并将响应结果保存到响应处理器中
            responseHandler.put(streamId, channel.write(request), channel.newPromise());
            channel.flush();
            // 等待HTTP/2响应并获取结果
            RpcResponse response = responseHandler.awaitResponses(60, TimeUnit.SECONDS);
            logger.info("response: " + response);

            // 断言检查响应内容是否为"Hello World"
            assertEquals("Hello World", response);

            logger.info("Finished HTTP/2 request(s)");

        } finally {
            // 关闭工作线程组
            workerGroup.shutdownGracefully();
        }
    }


    @After
    public void cleanup() {
        channel.close()
                .syncUninterruptibly();
    }
}
