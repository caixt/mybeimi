package com.github.cxt.mybeimi.core.engine.game.task.dizhu;

import java.util.List;

import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.ActionTaskUtils;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameEvent;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameTask;
import com.github.cxt.mybeimi.core.engine.game.GameBoard;
import com.github.cxt.mybeimi.core.engine.game.task.AbstractTask;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.util.rules.model.DuZhuBoard;
import com.github.cxt.mybeimi.util.rules.model.Player;
import com.github.cxt.mybeimi.web.model.GameRoom;
import com.github.cxt.mybeimi.web.model.PlayUserClient;

/**
 * 抢地主
 * @author iceworld
 *
 */
public class CreateAutoTask extends AbstractTask implements BeiMiGameTask{

	private long timer  ;
	private GameRoom gameRoom = null ;
	
	public CreateAutoTask(long timer , GameRoom gameRoom){
		super();
		this.timer = timer ;
		this.gameRoom = gameRoom ;
	}
	@Override
	public long getCacheExpiryTime() {
		return System.currentTimeMillis()+timer*1000;	//5秒后执行
	}
	
	public void execute(){
		System.out.println("!!!!!!!!!");
		DuZhuBoard board = (DuZhuBoard) CacheHelper.getBoardCacheBean().getCacheObject(gameRoom.getId());
		Player randomCardPlayer = null , catchPlayer = null;
		int index = 0 ;
		if(board!=null){
			/**
			 * 抢地主，首个抢地主的人 在发牌的时候已经生成
			 */
			for(int i=0 ; i<board.getPlayers().length ; i++){
				Player player = board.getPlayers()[i] ;
				if(player.isRandomcard()){
					randomCardPlayer = player ;
					index = i;
					break ;
				}
			}
			if(randomCardPlayer.isDocatch()){
				catchPlayer = board.next(index);
			}else{
				catchPlayer = randomCardPlayer;
			}
			if(catchPlayer == null && randomCardPlayer.isRecatch() == false && !board.getBanker().equals(randomCardPlayer.getPlayuser())){
				//
				catchPlayer = randomCardPlayer ;	//起到地主牌的人第二次抢地主 ， 抢完就结束了
				randomCardPlayer.setRecatch(true);
			}
		}
		/**
		 * 地主抢完了即可进入玩牌的流程了，否则，一直发送 AUTO事件，进行抢地主
		 */
		if(catchPlayer!=null){
			boolean isNormal = true ;
			List<PlayUserClient> users = CacheHelper.getGamePlayerCacheBean().getCacheObject(gameRoom.getId()) ;
			for(PlayUserClient playUser : users){
				if(catchPlayer.getPlayuser().equals(playUser.getId())){
					if(!playUser.getPlayertype().equals(BMDataContext.PlayerTypeEnum.NORMAL.toString())){
						//AI或托管，自动抢地主，后台配置 自动抢地主的触发时间，或者 抢还是不抢， 无配置的情况下，默认的是抢地主
						isNormal = false ;
						/**
						 * 根据 配置参数获取是否选择叫地主
						 */
						board = ActionTaskUtils.doCatch(board, catchPlayer , true) ;
						break ;
					}
				}
			}
			catchPlayer.setDocatch(true);//抢过了
//			board.setBanker(catchPlayer.getPlayuser());	//玩家 点击 抢地主按钮后 赋值
			sendEvent("catch", new GameBoard(catchPlayer.getPlayuser() , board.isDocatch() , catchPlayer.isAccept() , board.getRatio()), gameRoom) ;
			
			if(isNormal){	//真人
				super.getGame(gameRoom.getPlayway()).change(gameRoom , BeiMiGameEvent.AUTO.toString() , 17);	//通知状态机 , 此处应由状态机处理异步执行
			}else{			//AI或托管
				sendEvent("catchresult", new GameBoard(catchPlayer.getPlayuser() , catchPlayer.isAccept(), catchPlayer.isAccept() , board.getRatio()) , gameRoom) ;
				super.getGame(gameRoom.getPlayway()).change(gameRoom , BeiMiGameEvent.AUTO.toString() , 2);	//通知状态机 , 此处应由状态机处理异步执行
				board.setDocatch(true);	//变成抢地主
			}
			
			CacheHelper.getBoardCacheBean().put(gameRoom.getId(), board);
		}else{
			//开始打牌，地主的人是最后一个抢了地主的人
			super.getGame(gameRoom.getPlayway()).change(gameRoom , BeiMiGameEvent.RAISEHANDS.toString());	//通知状态机 , 全部都抢过地主了 ， 把底牌发给 最后一个抢到地主的人
		}
	}
}
