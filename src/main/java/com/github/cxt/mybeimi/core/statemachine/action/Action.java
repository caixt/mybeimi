package com.github.cxt.mybeimi.core.statemachine.action;

import com.github.cxt.mybeimi.core.statemachine.impl.BeiMiExtentionTransitionConfigurer;
import com.github.cxt.mybeimi.core.statemachine.message.Message;

public interface Action<T,S> {
	void execute(Message<T> message , BeiMiExtentionTransitionConfigurer<T, S> configurer); 
}
