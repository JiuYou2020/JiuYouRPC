package cn.jiuyou.netty;


import cn.jiuyou.ServiceProvider;
import cn.jiuyou.codec.MyDecoder;
import cn.jiuyou.codec.MyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 13:47
 * {@code @Description: } 初始化，主要负责序列化的编码解码， 需要解决netty的粘包问题
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private final ServiceProvider serviceProvider;

    public NettyServerInitializer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        // 使用自定义的解码器
        pipeline.addLast(new MyDecoder());
        // 使用自定义的编码器
        pipeline.addLast(new MyEncoder());

        pipeline.addLast(new NettyRpcServerHandler(serviceProvider));
    }
}
