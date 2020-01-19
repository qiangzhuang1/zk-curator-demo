package com.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class LockDemo {
    public static final String IP_ADDR = "192.168.32.129:2181,192.168.32.133:2181";

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(IP_ADDR).sessionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3)).build();
        curatorFramework.start();
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, "/locks");
        for(int i=0;i<10;i++){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"->尝试获取锁");
                try {
                    lock.acquire();
                    System.out.println(Thread.currentThread().getName()+"->获得锁成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                    lock.release();
                    System.out.println(Thread.currentThread().getName()+"->释放锁成功");
                } catch (Exception e){
                    e.printStackTrace();
                }
            },"t"+i).start();
        }
    }
}