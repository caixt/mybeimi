package com.github.cxt.mybeimi.core.engine.game.task;

import java.util.List;
import org.cache2k.expiry.ValueWithExpiryTime;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameTask;
import com.github.cxt.mybeimi.core.engine.game.model.Summary;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.util.rules.model.Board;
import com.github.cxt.mybeimi.util.rules.model.Player;
import com.github.cxt.mybeimi.web.model.GamePlayway;
import com.github.cxt.mybeimi.web.model.GameRoom;
import com.github.cxt.mybeimi.web.model.PlayUserClient;

public class CreateAllCardsTask extends AbstractTask implements ValueWithExpiryTime  , BeiMiGameTask{

	private long timer  ;
	private GameRoom gameRoom = null ;
	
	public CreateAllCardsTask(long timer , GameRoom gameRoom){
		super();
		this.timer = timer ;
		this.gameRoom = gameRoom ;
	}
	@Override
	public long getCacheExpiryTime() {
		return System.currentTimeMillis()+timer*1000;	//5秒后执行
	}
	
	public void execute(){
		Board board = (Board) CacheHelper.getBoardCacheBean().getCacheObject(gameRoom.getId());
		board.setFinished(true);
		GamePlayway gamePlayWay = (GamePlayway) CacheHelper.getSystemCacheBean().getCacheObject(gameRoom.getPlayway()) ;
		if(gamePlayWay!=null){
			/**
			 * 结算信息 ， 更新 玩家信息
			 */
			Summary summary = board.summary(board, gameRoom, gamePlayWay) ;
			sendEvent("allcards",  summary , gameRoom) ;	//通知所有客户端结束牌局，进入结算
			if(summary.isGameRoomOver()){
				CacheHelper.getGamePlayerCacheBean().delete(gameRoom.getId()) ;
//				for(Player player : board.getPlayers()){
//					CacheHelper.getRoomMappingCacheBean().delete(player.getPlayuser()) ;
//				}
				/**
				 * 重新加入房间资源到 队列
				 */
				CacheHelper.getQueneCache(gameRoom.getPlayway()).offer(gameRoom);
			}
		}

		List<PlayUserClient> players = CacheHelper.getGamePlayerCacheBean().getCacheObject(gameRoom.getId()) ;
		for(PlayUserClient player : players){
			if(CacheHelper.getApiUserCacheBean().getCacheObject(player.getId())!=null){
				player.setGamestatus(BMDataContext.GameStatusEnum.NOTREADY.toString());
				CacheHelper.getGamePlayerCacheBean().put(player.getId(),player) ;
			}
		}
		
		BMDataContext.getGameEngine().finished(gameRoom.getId());
	}
}
