package com.github.cxt.mybeimi.core.engine.game.task;

import com.github.cxt.mybeimi.config.web.model.Game;
import com.github.cxt.mybeimi.core.engine.game.ActionTaskUtils;
import com.github.cxt.mybeimi.core.engine.game.Message;
import com.github.cxt.mybeimi.util.GameUtils;
import com.github.cxt.mybeimi.web.model.GameRoom;


public abstract class AbstractTask {

	/**
	 * 根据玩法，找到对应的状态机
	 * @param playway
	 * @param orgi
	 * @return
	 */
	public Game getGame(String playway){
		return GameUtils.getGame(playway) ;
	}
	
	public void sendEvent(String event , Message message , GameRoom gameRoom){
		ActionTaskUtils.sendEvent(event, message, gameRoom);
	}
}
