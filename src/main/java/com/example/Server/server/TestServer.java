package com.example.Server.server;

import com.example.Server.provider.ServiceProvider;
import com.example.Server.server.impl.SimpleRPCServer;
import com.example.common.Service.UserService;
import com.example.common.Service.impl.UserServiceImpl;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);
        RpcServer rpcServer = new SimpleRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
