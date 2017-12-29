package com.github.cxt.mybeimi.config.web;

import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.cxt.mybeimi.config.web.model.Game;
import com.github.cxt.mybeimi.core.statemachine.BeiMiStateMachine;
import com.github.cxt.mybeimi.core.statemachine.impl.BeiMiMachineHandler;

@Configuration
public class BeiMiStateMachineHandlerConfig {
	
	@Resource(name="dizhu")    
	private BeiMiStateMachine<String,String> dizhuConfigure ;
	
	
    @Bean("dizhuGame")
    public Game dizhu() {
        return new Game(new BeiMiMachineHandler(this.dizhuConfigure));
    }
}
