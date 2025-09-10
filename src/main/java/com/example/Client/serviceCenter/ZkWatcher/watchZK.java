package com.example.Client.serviceCenter.ZkWatcher;

import com.example.Client.cache.serviceCache;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

public class watchZK {
    private CuratorFramework client;
    private serviceCache cache;

    public watchZK(CuratorFramework client, serviceCache cache) {
        this.client = client;
        this.cache = cache;
    }

    public void watchToUpdate(String path) throws InterruptedException {
        //监视指定路径下的节点变化,并在节点变化时更新本地缓存
        //CuratorCache时Curator提供的一个用与监听节点变化的API
        //他会监听指定路径节点变化，这里监听的是根路径
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        //注册一个监听器，用于处理节点变化
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            //参数：事件类型（枚举），节点更新前的状态、数据，节点更新后的数据、状态，
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                switch (type.name()) {
                    case "NODE_CREATED":
                        String[] pathList = parsePath(childData1);
                        if (pathList.length <= 2) break;
                        else {
                            String serviceName = pathList[1];
                            String address = pathList[2];
                            cache.addServiceToCache(serviceName, address);
                        }
                        break;
                    case "NODE_CHANGE":
                        if (childData.getData() != null) {
                            System.out.println("修改前的数据"+new String(childData.getData()));
                        } else {
                            System.out.println("节点第一次赋值");
                        }
                        String[] oldPathList = parsePath(childData);
                        String[] newPathList = parsePath(childData1);
                        cache.replaceServiceAddress(oldPathList[1], oldPathList[2], newPathList[2]);;
                        System.out.println("修改后的数据"+ new String(childData1.getData()));
                        break;
                    case "NODE_DELETED":
                        String[] pathList_d = parsePath(childData);
                        if (pathList_d.length <= 2) break;
                        else {
                            String serviceName = pathList_d[1];
                            String address = pathList_d[2];
                            cache.delete(serviceName, address);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        curatorCache.start();
    }

    private String[] parsePath(ChildData childData) {
        String path = new String(childData.getData());
        return path.split("/");
    }
}
