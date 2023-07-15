package cn.jiuyou.impl.BIO;


import cn.jiuyou.Server;
import cn.jiuyou.ServiceProvider;
import cn.jiuyou.entity.RpcRequest;
import cn.jiuyou.entity.RpcResponse;
import cn.jiuyou.impl.AccountServiceImpl;
import cn.jiuyou.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

import static cn.jiuyou.constant.Constants.PORT;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 01:41
 * {@code @Description: } Server的简单实现，BIO同步阻塞式IO
 */
@Slf4j
public class SimpleServer implements Server {
    public void run() {
        //初始化ServiceProvider
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.addService(new UserServiceImpl());
        serviceProvider.addService(new AccountServiceImpl());

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            //以BIO方式接收请求
            while (true) {
                Socket socket = serverSocket.accept();
                //获取RpcRequest
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
                //根据interfaceName拿到对应的Service对象
                Object serviceImpl = serviceProvider.getService(rpcRequest.getInterfaceName());
                //根据methodName找到目标方法
                Method method = serviceImpl.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                //执行目标方法
                Object res = method.invoke(serviceImpl, rpcRequest.getParams());
                //封装RpcResponse
                RpcResponse rpcResponse = RpcResponse.success(res);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(rpcResponse);
                outputStream.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("server启动失败");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            log.error("server中没有目标方法");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            log.error("目标方法执行失败");
        } catch (IllegalAccessException e) {
            log.error("无权限执行目标方法");
        }
    }

    @Override
    public void stop() {

    }
}
