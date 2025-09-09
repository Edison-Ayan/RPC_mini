package com.example.Server.provider;

import com.example.Server.serviceRegister.ServiceRegister;
import com.example.Server.serviceRegister.impl.ZKServiceRegister;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServiceProvider {
    private Map<String, Object> interfaceProvider;
    private int port;
    private String host;
    private ServiceRegister serviceRegister;
    public ServiceProvider(String host, int port) {
        this.interfaceProvider = new HashMap<>();
        this.host = host;
        this.port = port;
        this.serviceRegister = new ZKServiceRegister();
    }

    public void provideServiceInterface(Object service) {
        String serviceName = service.getClass().getName();
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for (Class<?> clazz:interfaceName) {
            interfaceProvider.put(clazz.getName(),service);
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port));
        }
    }

    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}
