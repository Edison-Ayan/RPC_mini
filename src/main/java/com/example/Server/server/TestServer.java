package com.example.Server.server;

import com.example.Server.provider.ServiceProvider;
import com.example.Server.server.impl.SimpleRPCServer;
import com.example.common.Service.UserService;
import com.example.common.Service.impl.UserServiceImpl;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1",9999);
        serviceProvider.provideServiceInterface(userService, true);
        RpcServer rpcServer = new SimpleRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
