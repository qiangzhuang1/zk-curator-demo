package com.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class CrudDemo {
    public static final String IP_ADDR = "192.168.32.129:2181,192.168.32.131:2181";

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(IP_ADDR).sessionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3)).build();
        curatorFramework.start();
        //createDate(curatorFramework);
        //deleteDate(curatorFramework);
        //updateDate(curatorFramework);
        getDate(curatorFramework);
    }
    //新建
    public static void createDate(CuratorFramework curatorFramework) throws Exception {
        //CreateMode.PERSISTENT 代表节点的类型。CreateMode枚举中还有其他节点的类型
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                forPath("/data/test","testValue".getBytes());
    }
    //删除
    public static void deleteDate(CuratorFramework curatorFramework) throws Exception {
        //如果下面有子目录的话直接删除 /data 是不成功的
        // 第一种删除方式  curatorFramework.delete().forPath("/data/test");

        //第二种方式指定 version 版本
        Stat stat = new Stat();
        //获取删除节点的值 String value = new String(curatorFramework.getData().storingStatIn(stat).forPath("/data/test"));
        curatorFramework.delete().withVersion(stat.getVersion()).forPath("/data/test");
    }
    //改
    public static void updateDate(CuratorFramework curatorFramework) throws Exception {
        curatorFramework.setData().forPath("/data/test", "testValueUpdate".getBytes());
    }
    //查
    public static void getDate(CuratorFramework curatorFramework) throws Exception {
        byte[] bytes = curatorFramework.getData().forPath("/data/test");
        System.out.println(new String(bytes));
    }
}