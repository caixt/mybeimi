package com.github.cxt.mybeimi.core.engine.game.task;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cache2k.expiry.ValueWithExpiryTime;

import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.ActionTaskUtils;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameEvent;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameTask;
import com.github.cxt.mybeimi.core.engine.game.impl.Banker;
import com.github.cxt.mybeimi.core.engine.game.impl.UserBoard;
import com.github.cxt.mybeimi.util.GameUtils;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.util.rules.model.Board;
import com.github.cxt.mybeimi.web.model.GameRoom;
import com.github.cxt.mybeimi.web.model.PlayUserClient;

public class CreateBeginTask extends AbstractTask implements ValueWithExpiryTime  , BeiMiGameTask{

	private long timer  ;
	private GameRoom gameRoom = null ;
	
	public CreateBeginTask(long timer , GameRoom gameRoom){
		super();
		this.timer = timer ;
		this.gameRoom = gameRoom ;
	}
	@Override
	public long getCacheExpiryTime() {
		return System.currentTimeMillis()+timer*1000;	//5秒后执行
	}
	
	public void execute(){
		List<PlayUserClient> playerList = CacheHelper.getGamePlayerCacheBean().getCacheObject(gameRoom.getId()) ;
		/**
		 * 
		 * 顺手 把牌发了，注：此处应根据 GameRoom的类型获取 发牌方式
		 */
		boolean inroom = false;
		if(!StringUtils.isBlank(gameRoom.getLastwinner())){
			for(PlayUserClient player : playerList){
				if(player.getId().equals(gameRoom.getLastwinner())){
					inroom = true ;
				}
			}
		}
		if(inroom == false){
			gameRoom.setLastwinner(playerList.get(0).getId());
		}
		/**
		 * 通知所有玩家 新的庄
		 */
		ActionTaskUtils.sendEvent("banker",  new Banker(gameRoom.getLastwinner()), gameRoom);
		
		Board board = GameUtils.playGame(playerList, gameRoom, gameRoom.getLastwinner(), gameRoom.getCardsnum()) ;
		CacheHelper.getBoardCacheBean().put(gameRoom.getId(), board);
		for(Object temp : playerList){
			PlayUserClient playerUser = (PlayUserClient) temp ;
			playerUser.setGamestatus(BMDataContext.GameStatusEnum.PLAYING.toString());
			/**
			 * 更新状态到 PLAYING
			 */
			if(CacheHelper.getApiUserCacheBean().getCacheObject(playerUser.getId())!=null){
				CacheHelper.getApiUserCacheBean().put(playerUser.getId(), playerUser);
			}
			/**
			 * 每个人收到的 牌面不同，所以不用 ROOM发送广播消息，而是用 遍历房间里所有成员发送消息的方式
			 */
			ActionTaskUtils.sendEvent(playerUser, new UserBoard(board , playerUser.getId() , "play"));
		}
		
		CacheHelper.getGameRoomCacheBean().put(gameRoom.getId(), gameRoom);
		
		
		/**
		 * 发送一个 Begin 事件
		 */
		super.getGame(gameRoom.getPlayway()).change(gameRoom , BeiMiGameEvent.AUTO.toString() , 2);	//通知状态机 , 此处应由状态机处理异步执行
	}
}
