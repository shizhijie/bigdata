package com.zjs.bwcx.bigdata.hadooprpc.client;

import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import com.zjs.bwcx.bigdata.hadooprpc.protocol.ClientNamenodeProtocol;

public class MyHdfsClient {
    
    public static void main(String[] args) throws Exception {
        
        ClientNamenodeProtocol proxy = RPC.getProxy(ClientNamenodeProtocol.class, 1L, new InetSocketAddress("localhost", 8888), new Configuration());
        String metaData = proxy.getMetaData("/angelababy.mygirl");
        System.out.println(metaData);
    }
    
}
