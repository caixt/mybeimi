package com.github.cxt.mybeimi.core.engine.game.action;

import com.github.cxt.mybeimi.core.statemachine.action.Action;
import com.github.cxt.mybeimi.core.statemachine.impl.BeiMiExtentionTransitionConfigurer;
import com.github.cxt.mybeimi.core.statemachine.message.Message;

/**
 * 创建房间的人，房卡模式下的 房主， 大厅模式下的首个进入房间的人
 * @author iceworld
 *
 */
public class JoinAction<T,S> implements Action<T, S>{
	
	/**
	 * JOIN事件，检查是否 凑齐一桌子，如果凑齐了，直接开始，并取消计时器
	 * 如果不够一桌子，啥也不做，等人活等计时器到事件
	 * 撮合成功的，立即开启游戏
	 * 通知所有成员的消息在 GameEventHandler里处理了
	 * 
	 */
	@Override
	public void execute(Message<T> message, BeiMiExtentionTransitionConfigurer<T,S> configurer) {
		System.out.println("!");
	}
}
