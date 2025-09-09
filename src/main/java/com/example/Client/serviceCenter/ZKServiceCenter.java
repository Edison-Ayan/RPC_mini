package com.example.Client.serviceCenter;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceCenter implements ServiceCenter{
    //curator提供的zookeeper客户端
    private CuratorFramework client;
    //zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";

    public ZKServiceCenter(){
        //指数时间重试,连接失败时，自动进行重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000,3);
        //zookeeper的地址固定
        //sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系
        //
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper连接成功");
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            //获取服务名对应路径下的所有子节点，子节点通常保存服务实例的地址
            List<String> strings = client.getChildren().forPath("/"+serviceName);
            //Todo:这里默认用第一个后面加负载均衡
            String string = strings.get(0);
            //将子节点解析为InetSocketAddress,便于客户端进行通信
            return parseAddress(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() +
                ":" +
                serverAddress.getPort();
    }

    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0],Integer.parseInt(result[1]));
    }

}
