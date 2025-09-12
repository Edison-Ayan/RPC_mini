package com.example.Client.serviceCenter;

import com.example.Client.cache.serviceCache;
import com.example.Client.serviceCenter.ZkWatcher.watchZK;
import com.example.Client.serviceCenter.balance.impl.ConsistencyHashBalance;
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
    private static final String RETRY = "CanRetry";
    //serviceCache
    private serviceCache cache;

    public ZKServiceCenter() throws InterruptedException {
        //指数时间重试,连接失败时，自动进行重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000,3);
        //zookeeper的地址固定
        //sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系
        //
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper连接成功");
        this.cache = new serviceCache();
        watchZK watcher = new watchZK(client, cache);
        watcher.watchToUpdate(ROOT_PATH);
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            List<String> serviceList = cache.getServiceFromCache(serviceName);
            if (serviceList == null) {
                serviceList = client.getChildren().forPath("/"+serviceName);
            }
            //获取服务名对应路径下的所有子节点，子节点通常保存服务实例的地址
            String address = new ConsistencyHashBalance().balance(serviceList);
            //将子节点解析为InetSocketAddress,便于客户端进行通信
            return parseAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean checkRetry(String serviceName) {
        boolean canRetry = false;
        try {
            List<String> serviceList = client.getChildren().forPath("/" + RETRY);
            for (String s: serviceList) {
                if (s.equals(serviceName)) {
                    System.out.println(serviceName+"可重试");
                    canRetry = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canRetry;
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
