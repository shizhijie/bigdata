package com.zjs.bwcx.bigdata.hadooprpc.service;

import com.zjs.bwcx.bigdata.hadooprpc.protocol.IUserLoginService;

public class UserLoginServiceImpl implements IUserLoginService{

	@Override
	public String login(String name, String passwd) {
		
		return name + "logged in successfully...";
	}
	
	
	

}
