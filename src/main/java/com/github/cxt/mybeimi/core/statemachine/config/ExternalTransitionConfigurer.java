package com.github.cxt.mybeimi.core.statemachine.config;

public interface ExternalTransitionConfigurer<S,E> extends TransitionConfigurer<ExternalTransitionConfigurer<S, E>, S, E>{
	
	public ExternalTransitionConfigurer<S, E> target(S target);
}
