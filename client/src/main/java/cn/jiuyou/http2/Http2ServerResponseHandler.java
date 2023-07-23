package cn.jiuyou.http2;

import cn.jiuyou.ServiceProvider;
import cn.jiuyou.constant.Payload;
import cn.jiuyou.entity.RpcRequest;
import cn.jiuyou.entity.RpcResponse;
import cn.jiuyou.serviceDiscovery.impl.ZookeeperServiceDiscovery;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.x.discovery.ServiceInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Sharable
@Slf4j
public class Http2ServerResponseHandler extends ChannelDuplexHandler {
    static final ByteBuf RESPONSE_BYTES = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断接收到的消息是否为HTTP/2头帧（Http2HeadersFrame）
        if (msg instanceof Http2HeadersFrame) {
            // 将接收到的消息转换为Http2HeadersFrame类型
            Http2HeadersFrame msgHeader = (Http2HeadersFrame) msg;

            // 检查头帧是否表示流的结束（即响应结束,Get请求）
            if (msgHeader.isEndStream()) {
                // 创建一个ByteBuf用于存放响应内容
                ByteBuf content = ctx.alloc().buffer();
                // 将RESPONSE_BYTES的副本写入ByteBuf中（这里假设RESPONSE_BYTES是响应内容的字节数组）
                content.writeBytes(RESPONSE_BYTES.duplicate());

                // 创建HTTP/2头部（Headers）对象，设置响应状态为200 OK
                Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());

                // 将响应头部和内容分别封装成DefaultHttp2HeadersFrame和DefaultHttp2DataFrame
                // 然后通过ctx（ChannelHandlerContext）写入到HTTP/2连接中，完成响应的发送
                // 设置这两个帧的streamId与收到的头帧的streamId一致，确保它们属于同一个HTTP/2流
                ctx.write(new DefaultHttp2HeadersFrame(headers).stream(msgHeader.stream()));
                ctx.write(new DefaultHttp2DataFrame(content, true).stream(msgHeader.stream()));
            } else {
                // 如果头帧不是结束流的帧，说明可能还有数据帧，不能直接返回响应，应该继续处理其他数据帧
                // 将头帧信息存储在ChannelHandlerContext的属性中，便于后续使用
                ctx.channel().attr(AttributeKey.valueOf("msgHeader")).set(msgHeader);
            }
        } else if (msg instanceof DefaultHttp2DataFrame) {
            // 从数据帧读取数据
            DefaultHttp2DataFrame data = (DefaultHttp2DataFrame) msg;
            String requestBody = data.content().toString(CharsetUtil.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            RpcRequest rpcRequest = objectMapper.readValue(requestBody, RpcRequest.class);

            // 获取之前存储的头帧信息
            Http2HeadersFrame msgHeader = (Http2HeadersFrame) ctx.channel().attr(AttributeKey.valueOf("msgHeader")).get();

            // 判断数据帧是否是结束帧
            boolean isEndFrame = data.isEndStream();

            RpcResponse response;
            if (isEndFrame) {
                // 如果是最后一帧，则处理完整的请求并获取响应
                response = getResponse(rpcRequest);
            } else {
                //TODO 如果不是最后一帧，则继续处理请求的数据部分，这里可以根据业务逻辑进行相应处理
//                response = processPartialRequestData(rpcRequest);
                response = null;
            }

            // 将响应对象转换为JSON格式
            String responseJson = objectMapper.writeValueAsString(response);

            // 创建一个ByteBuf用于存放响应内容
            ByteBuf content = ctx.alloc().buffer();
            content.writeBytes(responseJson.getBytes());

            // 创建HTTP/2头部（Headers）对象，设置响应状态为200 OK
            Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());

            // 将响应头部和内容封装成DefaultHttp2HeadersFrame和DefaultHttp2DataFrame
            // 然后通过ctx（ChannelHandlerContext）写入到HTTP/2连接中，完成响应的发送
            // 设置这两个帧的streamId与之前存储的头帧的streamId一致，确保它们属于同一个HTTP/2流
            ctx.write(new DefaultHttp2HeadersFrame(headers).stream(msgHeader.stream()));
            ctx.write(new DefaultHttp2DataFrame(content, isEndFrame).stream(msgHeader.stream()));

            // 如果是最后一帧，则清除之前存储的头帧信息
            if (isEndFrame) {
                ctx.channel().attr(AttributeKey.valueOf("msgHeader")).set(null);
            }
        } else {
            // 如果不是HTTP/2头帧或数据帧，调用父类的channelRead方法，继续处理其他类型的消息
            super.channelRead(ctx, msg);
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    RpcResponse getResponse(RpcRequest request) throws Exception {
        //获取Discovery
        ZookeeperServiceDiscovery discovery = ZookeeperServiceDiscovery.getInstance();
        // 得到服务名
        String interfaceName = request.getInterfaceName();
        //增加连接数
        ServiceInstance<Payload> serviceInstance = ServiceProvider.getServiceInstance(interfaceName);
        serviceInstance.getPayload().incrementActiveCount();
        discovery.updateService(serviceInstance);
        log.info("服务实例:{}  活跃数+1 ，当前活跃数:{}", serviceInstance, serviceInstance.getPayload().getActiveCount());
        // 得到服务端相应服务实现类
        Object service = ServiceProvider.getService(interfaceName);
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
