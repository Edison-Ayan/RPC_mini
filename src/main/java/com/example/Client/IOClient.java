package com.example.Client;

import com.example.common.Message.RpcRequest;
import com.example.common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IOClient {
    public static RpcResponse sendRequest(String host, int port, RpcRequest request ) {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(request);
            oos.flush();
            //序列化request发送给服务端，刷新输出流以确保数据完全发送

            RpcResponse response = (RpcResponse) ois.readObject();
            return response;
        } catch (IOException  | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }
}
