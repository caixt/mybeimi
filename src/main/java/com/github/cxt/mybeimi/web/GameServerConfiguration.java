package com.github.cxt.mybeimi.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;


@org.springframework.context.annotation.Configuration
public class GameServerConfiguration {

	@Value("${socketio.server.host}")  
    private String host;  
  
    @Value("${socketio.server.port}")  
    private Integer port;
    
    @Value("${socketio.server.threads}")
    private String threads;
	
	
	@Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
//        config.setHostname(host);
        config.setPort(port);
        
		/**
		 * 性能优化
		 */
		config.getSocketConfig().setReuseAddress(true);
		config.getSocketConfig().setSoLinger(0);
		config.getSocketConfig().setTcpNoDelay(true);
		config.getSocketConfig().setTcpKeepAlive(true);
        
        SocketIOServer server =  new SocketIOServer(config);
        return server;
    }
}
