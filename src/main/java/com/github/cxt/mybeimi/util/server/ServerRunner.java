package com.github.cxt.mybeimi.util.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.util.server.handler.GameEventHandler;
  
@Component  
public class ServerRunner implements CommandLineRunner {  
    private final SocketIOServer server;
    
    @Autowired  
    public ServerRunner(SocketIOServer server) {  
        this.server = server;  
    }
    
    @Bean(name="gameEventHandler")
    public GameEventHandler gameEventHandler(){
    	return new GameEventHandler();
    }
    
    @Bean(name="gameNamespace")
    public SocketIONamespace gameSocketIONameSpace(SocketIOServer server, GameEventHandler gameEventHandler){
    	SocketIONamespace namespace = server.addNamespace(BMDataContext.NameSpaceEnum.GAME.getNamespace());
    	namespace.addListeners(gameEventHandler);
    	return namespace;
    }
    
    public void run(String... args) throws Exception { 
        server.start();  
        BMDataContext.setIMServerStatus(true);	//IMServer 启动成功
    }  
}  