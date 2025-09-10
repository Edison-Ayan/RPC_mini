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

public class ClientProxy implements InvocationHandler {

    private RpcClient rpcClient;

    public ClientProxy() throws InterruptedException{
        rpcClient = new NettyRpcClient();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes()).build();
        RpcResponse response = rpcClient.sendRequest(request);
        return response.getData();
    }

    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz}, this);
        return (T)o;
    }
}
