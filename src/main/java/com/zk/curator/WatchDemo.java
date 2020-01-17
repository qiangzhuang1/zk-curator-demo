package com.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class WatchDemo {
    public static final String IP_ADDR = "192.168.32.129:2181,192.168.32.131:2181";

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(IP_ADDR).sessionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3)).build();
        curatorFramework.start();
        //addListenerWithChild(curatorFramework);
        //addListenerWithNode(curatorFramework);
        treeWithNode(curatorFramework);
        System.in.read();
    }

    //针对于当前节点变化的监听
    private static void addListenerWithNode(CuratorFramework curatorFramework) throws Exception {
        NodeCache nodeCache = new NodeCache(curatorFramework, "/data-demo", false);
        NodeCacheListener nodeCacheListener = () -> {
            System.out.println(nodeCache.getCurrentData().getPath() + "---" + new String(nodeCache.getCurrentData().getData()));
        };
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }

    //针对于子节点的创建、删除和更新 触发事件
    private static void addListenerWithChild(CuratorFramework curatorFramework) throws Exception {
        PathChildrenCache nodeCache = new PathChildrenCache(curatorFramework, "/data-demo", true);
        PathChildrenCacheListener nodeCacheListener= (curatorFramework1, pathChildrenCacheEvent) -> {
            System.out.println(pathChildrenCacheEvent.getType() + "->" + new String(pathChildrenCacheEvent.getData().getData()));
        };
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start(PathChildrenCache.StartMode.NORMAL);
    }

    //监听自身节点的变化以及子节点的变化
    private static void treeWithNode(CuratorFramework curatorFramework) throws Exception {
        TreeCache treeCache = new TreeCache(curatorFramework,"/data-demo");
        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) {
                System.out.println("更改类型treeCache------Type:" + treeCacheEvent.getType() + "");
                System.out.println("节点:"+treeCacheEvent.getData().getPath());
                System.out.println("值:"+new String(treeCacheEvent.getData().getData()));
            }
        };
        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
    }
}