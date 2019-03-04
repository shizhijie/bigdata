package com.zjs.bwcx.bigdata.hadooprpc.client;

import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import com.zjs.bwcx.bigdata.hadooprpc.protocol.IUserLoginService;

public class UserLoginAction {
	public static void main(String[] args) throws Exception {
		IUserLoginService userLoginService = RPC.getProxy(IUserLoginService.class, 200L, new InetSocketAddress("localhost", 9999), new Configuration());
		String login = userLoginService.login("angelababy", "1314520");
		System.out.println(login);
		return;
	}
}
