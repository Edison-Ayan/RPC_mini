package com.example.Client.proxy;

import ch.qos.logback.core.net.server.Client;
import com.example.Client.IOClient;
import com.example.Client.rpcClient.RpcClient;
import com.example.Client.rpcClient.impl.NettyRpcClient;
import com.example.Client.rpcClient.impl.SimpleSocketRpcClient;
import com.example.common.Message.RpcRequest;
import com.example.common.Message.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    private String host;
    private int port;
    private RpcClient rpcClient;
    public ClientProxy(String host, int port, int choose) {
        switch (choose) {
            case 0:
                rpcClient = new NettyRpcClient(host, port);
                break;
            case 1:
                rpcClient = new SimpleSocketRpcClient(host, port);
        }
    }
    public ClientProxy(String host, int port) {
        rpcClient = new NettyRpcClient(host, port);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();
        RpcResponse response = IOClient.sendRequest(host, port, request);
        return response.getData();
    }

    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz}, this);
        return (T)o;
    }
}
