package com.github.cxt.mybeimi.util.client;

import com.github.cxt.mybeimi.util.server.handler.BeiMiClient;

public interface NettyClient {
	
	public BeiMiClient getClient(String key) ;
	
	public void putClient(String key , BeiMiClient client) ;
	
	public void removeClient(String key) ;
}
