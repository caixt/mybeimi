package com.github.cxt.mybeimi.core.engine.game.task.dizhu;

import org.cache2k.expiry.ValueWithExpiryTime;

import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameTask;
import com.github.cxt.mybeimi.core.engine.game.task.AbstractTask;
import com.github.cxt.mybeimi.web.model.GameRoom;

/**
 * 出牌计时器，默认25秒，超时执行
 * @author zhangtianyi
 *
 */
public class CreatePlayCardsTask extends AbstractTask implements ValueWithExpiryTime  , BeiMiGameTask{

	private long timer  ;
	private GameRoom gameRoom = null ;
	private String player ;
	
	public CreatePlayCardsTask(long timer ,String userid, GameRoom gameRoom){
		super();
		this.timer = timer ;
		this.gameRoom = gameRoom ;
		this.player = userid ;
	}
	@Override
	public long getCacheExpiryTime() {
		return System.currentTimeMillis()+timer*1000;	//5秒后执行
	}
	
	public void execute(){
		/**
		 * 合并代码，玩家 出牌超时处理和 玩家出牌统一使用一处代码
		 */
		BMDataContext.getGameEngine().takeCardsRequest(this.gameRoom.getId(), this.player ,true,  null);
	}
}
