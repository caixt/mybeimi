package com.github.cxt.mybeimi.core.engine.game.action.dizhu;

import org.apache.commons.lang3.StringUtils;
import com.github.cxt.mybeimi.core.engine.game.task.dizhu.CreateRaiseHandsTask;
import com.github.cxt.mybeimi.core.statemachine.action.Action;
import com.github.cxt.mybeimi.core.statemachine.impl.BeiMiExtentionTransitionConfigurer;
import com.github.cxt.mybeimi.core.statemachine.message.Message;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.web.model.GameRoom;

/**
 * 反底牌发给地主
 * @author iceworld
 *
 * @param <T>
 * @param <S>
 */
public class RaiseHandsAction<T,S> implements Action<T, S>{

	@Override
	public void execute(Message<T> message, BeiMiExtentionTransitionConfigurer<T,S> configurer) {
		String room = (String)message.getMessageHeaders().getHeaders().get("room") ;
		if(!StringUtils.isBlank(room)){
			GameRoom gameRoom = (GameRoom) CacheHelper.getGameRoomCacheBean().getCacheObject(room) ; 
			if(gameRoom!=null){
				CacheHelper.getExpireCache().put(gameRoom.getId(), new CreateRaiseHandsTask(0 , gameRoom));
			}
		}
	}
}
