package com.github.cxt.mybeimi.core.engine.game.action;

import com.github.cxt.mybeimi.core.statemachine.action.Action;
import com.github.cxt.mybeimi.core.statemachine.impl.BeiMiExtentionTransitionConfigurer;
import com.github.cxt.mybeimi.core.statemachine.message.Message;

public class EventAction<T,S> implements Action<T, S>{

	@Override
	public void execute(Message<T> message, BeiMiExtentionTransitionConfigurer<T,S> configurer) {
		
	}
}
