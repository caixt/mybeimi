package com.github.cxt.mybeimi.core.engine.game.action.dizhu;


import org.apache.commons.lang3.StringUtils;

import com.github.cxt.mybeimi.core.engine.game.task.dizhu.CreateAutoTask;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.web.model.GameRoom;
import com.github.cxt.mybeimi.core.statemachine.action.Action;
import com.github.cxt.mybeimi.core.statemachine.impl.BeiMiExtentionTransitionConfigurer;
import com.github.cxt.mybeimi.core.statemachine.message.Message;

/**
 * 凑够了，开牌
 * @author iceworld
 *
 */
public class AutoAction<T,S> implements Action<T, S>{
	@Override
	public void execute(Message<T> message , BeiMiExtentionTransitionConfigurer<T,S> configurer) {
		String room = (String)message.getMessageHeaders().getHeaders().get("room") ;
		if(!StringUtils.isBlank(room)){
			GameRoom gameRoom = (GameRoom) CacheHelper.getGameRoomCacheBean().getCacheObject(room) ; 
			if(gameRoom!=null){
				int interval = (int) message.getMessageHeaders().getHeaders().get("interval") ;
				CacheHelper.getExpireCache().put(gameRoom.getId(), new CreateAutoTask(interval , gameRoom));
			}
		}
	}
}
