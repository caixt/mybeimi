package com.github.cxt.mybeimi.core.engine.game.task;

import java.util.Iterator;
import java.util.List;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.ActionTaskUtils;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameEvent;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameTask;
import com.github.cxt.mybeimi.util.GameUtils;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.web.model.GameRoom;
import com.github.cxt.mybeimi.web.model.PlayUser;
import com.github.cxt.mybeimi.web.model.PlayUserClient;

public class CreateAITask extends AbstractTask implements  BeiMiGameTask{

	private long timer  ;
	private GameRoom gameRoom = null ;
	
	public CreateAITask(long timer , GameRoom gameRoom){
		super();
		this.timer = timer ;
		this.gameRoom = gameRoom ;
	}
	@Override
	public long getCacheExpiryTime() {
		return System.currentTimeMillis()+timer*1000;	//5秒后执行
	}
	
	public void execute(){
		//执行生成AI
		boolean b = GameUtils.removeGameRoom(gameRoom);
		System.out.println(b);
		List<PlayUserClient> playerList = CacheHelper.getGamePlayerCacheBean().getCacheObject(gameRoom.getId()) ;
		/**
		 * 清理 未就绪玩家
		 */
		Iterator<PlayUserClient> iterator =  playerList.iterator();
		while(iterator.hasNext()){
			PlayUserClient player = iterator.next();
			if(!player.getGamestatus().equals(BMDataContext.GameStatusEnum.READY.toString())){
				iterator.remove();
				CacheHelper.getGamePlayerCacheBean().delete(gameRoom.getId(), player) ;
			}
		}
		int aicount = gameRoom.getPlayers() - playerList.size() ;
		if(aicount>0){
			for(int i=0 ; i<aicount ; i++){
				PlayUserClient playerUser = GameUtils.create(new PlayUser() , BMDataContext.PlayerTypeEnum.AI.toString()) ;
				playerUser.setPlayerindex(System.currentTimeMillis());	//按照加入房间的时间排序，有玩家离开后，重新发送玩家信息列表，重新座位
				CacheHelper.getGamePlayerCacheBean().put(gameRoom.getId(), playerUser); //将用户加入到 room ， MultiCache
				playerList.add(playerUser) ;
			}
		}
		/**
		 * 发送一个 Enough 事件
		 */
		ActionTaskUtils.sendPlayers(gameRoom, playerList);
		
		super.getGame(gameRoom.getPlayway()).change(gameRoom , BeiMiGameEvent.ENOUGH.toString());	//通知状态机 , 此处应由状态机处理异步执行
	}
}
