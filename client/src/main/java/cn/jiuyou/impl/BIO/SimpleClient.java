package cn.jiuyou.impl.BIO;


import cn.jiuyou.Client;
import cn.jiuyou.entity.RpcRequest;
import cn.jiuyou.entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static cn.jiuyou.constant.Constants.HOST;
import static cn.jiuyou.constant.Constants.PORT;


/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/24 21:20
 * {@code @Description: }
 */
@Slf4j
public class SimpleClient implements Client {
    public RpcResponse call(RpcRequest rpcRequest) {
        //连接Server
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            //发送RpcRequest
            outputStream.writeObject(rpcRequest);
            outputStream.flush();
            return (RpcResponse) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("client启动失败");
        }
        return null;
    }
}
