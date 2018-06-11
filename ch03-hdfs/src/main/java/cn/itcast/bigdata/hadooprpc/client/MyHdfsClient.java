package cn.itcast.bigdata.hadooprpc.client;

import cn.itcast.bigdata.hadooprpc.protocol.ClientNamenodeProtocol;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.net.InetSocketAddress;

public class MyHdfsClient {

    public static void main(String[] args) throws Exception {
        ClientNamenodeProtocol namenode = RPC.getProxy(ClientNamenodeProtocol.class,
                1L, // 这里指定的协议版本号要跟接口中的versionID的值保持一致
                new InetSocketAddress("localhost", 8888), new Configuration());
        String metaData = namenode.getMetaData("testRpc.log");
        System.out.println(metaData);
    }

}
