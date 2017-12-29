package com.github.cxt.mybeimi.core.engine.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.state.GameEvent;
import com.github.cxt.mybeimi.core.engine.game.task.majiang.CreateMJRaiseHandsTask;
import com.github.cxt.mybeimi.util.GameUtils;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.util.client.NettyClients;
import com.github.cxt.mybeimi.util.rules.model.Action;
import com.github.cxt.mybeimi.util.rules.model.ActionEvent;
import com.github.cxt.mybeimi.util.rules.model.Board;
import com.github.cxt.mybeimi.util.rules.model.DuZhuBoard;
import com.github.cxt.mybeimi.util.rules.model.JoinRoom;
import com.github.cxt.mybeimi.util.rules.model.Player;
import com.github.cxt.mybeimi.util.rules.model.RecoveryData;
import com.github.cxt.mybeimi.util.rules.model.SelectColor;
import com.github.cxt.mybeimi.util.rules.model.TakeCards;
import com.github.cxt.mybeimi.util.server.handler.BeiMiClient;
import com.github.cxt.mybeimi.web.model.GamePlayway;
import com.github.cxt.mybeimi.web.model.GameRoom;
import com.github.cxt.mybeimi.web.model.PlayUserClient;


@Service(value="beimiGameEngine")
public class GameEngine {
	
	
	/**
	 * 检查是否所有玩家 都已经处于就绪状态，如果所有玩家都点击了 继续开始游戏，则发送一个 ALL事件，继续游戏，
	 * 否则，等待10秒时间，到期后如果玩家还没有就绪，就将该玩家T出去，等待新玩家加入
	 * @param roomid
	 * @param userid
	 * @param orgi
	 * @return
	 */
	public void restartRequest(String roomid , String userid , BeiMiClient beiMiClient){
		GameRoom gameRoom = (GameRoom) CacheHelper.getGameRoomCacheBean().getCacheObject(roomid) ;
		boolean notReady = false ;
		List<PlayUserClient> playerList = CacheHelper.getGamePlayerCacheBean().getCacheObject(gameRoom.getId()) ;
		if(playerList!=null && playerList.size() > 0){
			/**
			 * 有一个 等待 
			 */
			for(int i=0; i<playerList.size() ; ){
				PlayUserClient player = playerList.get(i) ;
				if(player.getPlayertype().equals(BMDataContext.PlayerTypeEnum.NORMAL.toString())){
					//普通玩家，当前玩家修改为READY状态
					PlayUserClient apiPlayUser = (PlayUserClient) CacheHelper.getApiUserCacheBean().getCacheObject(player.getId()) ;
					if(player.getId().equals(userid)){
						player.setGamestatus(BMDataContext.GameStatusEnum.READY.toString());
						/**
						 * 更新状态
						 */
						CacheHelper.getApiUserCacheBean().put(player.getId(), apiPlayUser);
					}else{//还有未就绪的玩家
						if(!player.getGamestatus().equals(BMDataContext.GameStatusEnum.READY.toString())){
							notReady = true ;
						}
					}
				}
				i++ ;
			}
		}
		if(notReady == true){
			/**
			 * 需要增加一个状态机的触发事件：等待其他人就绪，超过5秒以后未就绪的，直接踢掉，然后等待机器人加入
			 */
			GameUtils.getGame(gameRoom.getPlayway()).change(gameRoom , BeiMiGameEvent.ENTER.toString() , 0);
		}else if(playerList == null || playerList.size() == 0){//房间已解散
			PlayUserClient userClient = (PlayUserClient) CacheHelper.getApiUserCacheBean().getCacheObject(userid) ;
			BMDataContext.getGameEngine().gameRequest(userid, beiMiClient.getPlayway(), beiMiClient.getRoom(), userClient , beiMiClient) ;
		}
	}
	/**
	 * 出牌，并校验出牌是否合规
	 * @param roomid
	 * 
	 * @param auto 是否自动出牌，超时/托管/AI会调用 = true
	 * @param userid
	 * @param orgi
	 * @return
	 */
	public SelectColor selectColorRequest(String roomid, String userid , String color){
		SelectColor selectColor = null ;
		GameRoom gameRoom = (GameRoom) CacheHelper.getGameRoomCacheBean().getCacheObject(roomid) ;
		if(gameRoom!=null){
			Board board = (Board) CacheHelper.getBoardCacheBean().getCacheObject(gameRoom.getId());
			if(board!=null){
				//超时了 ， 执行自动出牌
//				Player[] players = board.getPlayers() ;
				/**
				 * 检查是否所有玩家都已经选择完毕 ， 如果所有人都选择完毕，即可开始
				 */
				selectColor = new SelectColor(board.getBanker());
				if(!StringUtils.isBlank(color)){
					if(!StringUtils.isBlank(color) && color.matches("[0-2]{1}")){
						selectColor.setColor(Integer.parseInt(color));
					}else{
						selectColor.setColor(0);
					}
					selectColor.setTime(System.currentTimeMillis());
					selectColor.setCommand("selectresult");
					
					selectColor.setUserid(userid);
				}
				boolean allselected = true ;
				for(Player ply : board.getPlayers()){
					if(ply.getPlayuser().equals(userid)){
						if(!StringUtils.isBlank(color) && color.matches("[0-2]{1}")){
							ply.setColor(Integer.parseInt(color));
						}else{
							ply.setColor(0);
						}
						ply.setSelected(true);
					}
					if(!ply.isSelected()){
						allselected = false ;
					}
				}
				CacheHelper.getBoardCacheBean().put(gameRoom.getId() , board);	//更新缓存数据
				ActionTaskUtils.sendEvent("selectresult", selectColor , gameRoom);	
				/**
				 * 检查是否全部都已经 定缺， 如果已全部定缺， 则发送 开打 
				 */
				if(allselected){
					/**
					 * 重置计时器，立即执行
					 */
					CacheHelper.getExpireCache().put(gameRoom.getId(), new CreateMJRaiseHandsTask(1 , gameRoom) );
					GameUtils.getGame(gameRoom.getPlayway()).change(gameRoom , BeiMiGameEvent.RAISEHANDS.toString() , 0);	
				}
			}
		}
		return selectColor ;
	}
	/**
	 * 麻将 ， 杠碰吃胡过
	 * @param roomid
	 * 
	 * @param userid
	 * @param orgi
	 * @return
	 */
	public ActionEvent actionEventRequest(String roomid, String userid, String action){
		ActionEvent actionEvent = null ;
		GameRoom gameRoom = (GameRoom) CacheHelper.getGameRoomCacheBean().getCacheObject(roomid) ;
		if(gameRoom!=null){
			Board board = (Board) CacheHelper.getBoardCacheBean().getCacheObject(gameRoom.getId());
			if(board!=null){
				Player player = board.player(userid) ;
				byte card = board.getLast().getCard() ;
				actionEvent = new ActionEvent(board.getBanker() , userid , card , action);
				if(!StringUtils.isBlank(action) && action.equals(BMDataContext.PlayerAction.GUO.toString())){
					/**
					 * 用户动作，选择 了 过， 下一个玩家直接开始抓牌 
					 * bug，待修复：如果有多个玩家可以碰，则一个碰了，其他玩家就无法操作了
					 */
					board.dealRequest(gameRoom, board , false , null);
				}else if(!StringUtils.isBlank(action) && action.equals(BMDataContext.PlayerAction.PENG.toString())){
					Action playerAction = new Action(userid , action , card);
					
					int color = card / 36 ;
					int value = card % 36 / 4 ;
					List<Byte> otherCardList = new ArrayList<Byte>(); 
					for(int i=0 ; i<player.getCards().length ; i++){
						if(player.getCards()[i]/36 == color && (player.getCards()[i]%36) / 4 == value){
							continue ;
						}
						otherCardList.add(player.getCards()[i]) ;
					}
					byte[] otherCards = new byte[otherCardList.size()] ;
					for(int i=0 ; i<otherCardList.size() ; i++){
						otherCards[i] = otherCardList.get(i) ;
					}
					player.setCards(otherCards);
					player.getActions().add(playerAction) ;
					
					board.setNextplayer(userid);
					
					actionEvent.setTarget(board.getLast().getUserid());
					ActionTaskUtils.sendEvent("selectaction", actionEvent , gameRoom);
					
					CacheHelper.getBoardCacheBean().put(gameRoom.getId() , board);	//更新缓存数据
					
					board.playcards(board, gameRoom, player);
					
				}else if(!StringUtils.isBlank(action) && action.equals(BMDataContext.PlayerAction.GANG.toString())){
					if(board.getNextplayer().equals(userid)){
						card = GameUtils.getGangCard(player.getCards()) ;
						actionEvent = new ActionEvent(board.getBanker() , userid , card , action);
						actionEvent.setActype(BMDataContext.PlayerGangAction.AN.toString());
					}else{
						actionEvent.setActype(BMDataContext.PlayerGangAction.MING.toString());	//还需要进一步区分一下是否 弯杠
					}
					/**
					 * 检查是否有弯杠
					 */
					Action playerAction = new Action(userid , action , card);
					for(Action ac : player.getActions()){
						if(ac.getCard() == card && ac.getAction().equals(BMDataContext.PlayerAction.PENG.toString())){
							ac.setGang(true);
							ac.setType(BMDataContext.PlayerGangAction.WAN.toString());
							playerAction = ac ;
							break ;
						}
					}
					int color = card / 36 ;
					int value = card % 36 / 4 ;
					List<Byte> otherCardList = new ArrayList<Byte>(); 
					for(int i=0 ; i<player.getCards().length ; i++){
						if(player.getCards()[i]/36 == color && (player.getCards()[i]%36) / 4 == value){
							continue ;
						}
						otherCardList.add(player.getCards()[i]) ;
					}
					byte[] otherCards = new byte[otherCardList.size()] ;
					for(int i=0 ; i<otherCardList.size() ; i++){
						otherCards[i] = otherCardList.get(i) ;
					}
					player.setCards(otherCards);
					player.getActions().add(playerAction) ;
					
					actionEvent.setTarget("all");	//只有明杠 是 其他人打出的 ， target 是单一对象
					
					ActionTaskUtils.sendEvent("selectaction", actionEvent , gameRoom);
					
					/**
					 * 杠了以后， 从 当前 牌的 最后一张开始抓牌
					 */
					board.dealRequest(gameRoom, board , true , userid);
				}
			}
		}
		return actionEvent ;
	}
	/**
	 * 抢地主，斗地主
	 * @param roomid
	 * @param userid
	 * @param orgi
	 * @return
	 */
	public void actionRequest(String roomid, PlayUserClient playUser , boolean accept){
		GameRoom gameRoom = (GameRoom) CacheHelper.getGameRoomCacheBean().getCacheObject(roomid) ;
		if(gameRoom!=null){
			DuZhuBoard board = (DuZhuBoard) CacheHelper.getBoardCacheBean().getCacheObject(gameRoom.getId());
			Player player = board.player(playUser.getId()) ;
			board = ActionTaskUtils.doCatch(board, player , accept) ;
			
			ActionTaskUtils.sendEvent("catchresult",new GameBoard(player.getPlayuser() , player.isAccept(), board.isDocatch() , board.getRatio()),gameRoom) ;
			GameUtils.getGame(gameRoom.getPlayway()).change(gameRoom , BeiMiGameEvent.AUTO.toString() , 15);	//通知状态机 , 继续执行
			
			CacheHelper.getBoardCacheBean().put(gameRoom.getId() , board) ;
			
			CacheHelper.getExpireCache().put(gameRoom.getId(), ActionTaskUtils.createAutoTask(1, gameRoom));
		}
	}
	/**
	 * 结束 当前牌局
	 * @param userid
	 * @param orgi
	 * @return
	 */
	public void finished(String roomid){
		if(!StringUtils.isBlank(roomid)){//
			CacheHelper.getExpireCache().remove(roomid);
			CacheHelper.getBoardCacheBean().delete(roomid) ;
		}
	}
	
	/**
	 * 出牌，并校验出牌是否合规
	 * @param roomid
	 * 
	 * @param auto 是否自动出牌，超时/托管/AI会调用 = true
	 * @param userid
	 * @param orgi
	 * @return
	 */
	public TakeCards takeCardsRequest(String roomid, String playUserClient , boolean auto , byte[] playCards){
		TakeCards takeCards = null ;
		GameRoom gameRoom = (GameRoom) CacheHelper.getGameRoomCacheBean().getCacheObject(roomid) ;
		if(gameRoom!=null){
			Board board = (Board) CacheHelper.getBoardCacheBean().getCacheObject(gameRoom.getId());
			Player player = board.player(playUserClient) ;
			
			if(board!=null && player.getPlayuser().equals(board.getNextplayer())){
				takeCards = board.takeCardsRequest(gameRoom, board, player, auto, playCards) ;
			}
		}
		return takeCards ;
	}
	
	public void gameRequest(String userid ,String playway , String room , PlayUserClient userClient , BeiMiClient beiMiClient ){
		GameEvent gameEvent = gameRequest(userClient.getId(), beiMiClient.getPlayway(), beiMiClient.getRoom(), userClient) ;
		if(gameEvent != null){
			/**
			 * 举手了，表示游戏可以开始了
			 */
			if(userClient!=null){
				userClient.setGamestatus(BMDataContext.GameStatusEnum.READY.toString());
			}
			/**
			 * 游戏状态 ， 玩家请求 游戏房间，活动房间状态后，发送事件给 StateMachine，由 StateMachine驱动 游戏状态 ， 此处只负责通知房间内的玩家
			 * 1、有新的玩家加入
			 * 2、给当前新加入的玩家发送房间中所有玩家信息（不包含隐私信息，根据业务需求，修改PlayUserClient的字段，剔除掉隐私信息后发送）
			 */
			ActionTaskUtils.sendEvent("joinroom", new JoinRoom(userClient, gameEvent.getIndex(), gameEvent.getGameRoom().getPlayers()) , gameEvent.getGameRoom());
			/**
			 * 发送给单一玩家的消息
			 */
			ActionTaskUtils.sendPlayers(beiMiClient, gameEvent.getGameRoom());
			/**
			 * 当前是在游戏中还是 未开始
			 */
			Board board = (Board) CacheHelper.getBoardCacheBean().getCacheObject(gameEvent.getRoomid());
			if(board !=null){
				Player currentPlayer = null;
				for(Player player : board.getPlayers()){
					if(player.getPlayuser().equals(userClient.getId())){
						currentPlayer = player ; break ;
					}
				}
				if(currentPlayer!=null){
					boolean automic = false ;
					if((board.getLast()!=null && board.getLast().getUserid().equals(currentPlayer.getPlayuser())) || (board.getLast() == null && board.getBanker().equals(currentPlayer.getPlayuser()))){
						automic = true ;
					}
					ActionTaskUtils.sendEvent("recovery", new RecoveryData(currentPlayer , board.getLasthands() , board.getNextplayer() , 25 , automic , board) , gameEvent.getGameRoom());
				}
			}else{
				//通知状态
				GameUtils.getGame(beiMiClient.getPlayway()).change(gameEvent);	//通知状态机 , 此处应由状态机处理异步执行
			}
		}
	}
	
	/**
	 * 玩家房间选择， 新请求，游戏撮合， 如果当前玩家是断线重连， 或者是 退出后进入的，则第一步检查是否已在房间
	 * 如果已在房间，直接返回
	 * @param userid
	 * @param room
	 * @param orgi
	 * @return
	 */
	public GameEvent gameRequest(String userid ,String playway , String room , PlayUserClient playUser){
		GameEvent gameEvent = null ;
		String roomid = (String) CacheHelper.getRoomMappingCacheBean().getCacheObject(userid) ;
		GamePlayway gamePlayway = (GamePlayway) CacheHelper.getSystemCacheBean().getCacheObject(playway) ;
		boolean needtakequene = false;
		if(gamePlayway!=null){
			gameEvent = new GameEvent(gamePlayway.getPlayers() , gamePlayway.getCardsnum()) ;
			GameRoom gameRoom = null ;
			if(!StringUtils.isBlank(roomid) && CacheHelper.getGameRoomCacheBean().getCacheObject(roomid)!=null){//
				gameRoom = (GameRoom) CacheHelper.getGameRoomCacheBean().getCacheObject(roomid) ;		//直接加入到 系统缓存 （只有一个地方对GameRoom进行二次写入，避免分布式锁）
			}else{
				if(!StringUtils.isBlank(room)){	//房卡游戏 , 创建ROOM
					gameRoom = this.creatGameRoom(gamePlayway, userid , true) ;
				}else{	//
					/**
					 * 大厅游戏 ， 撮合游戏 , 发送异步消息，通知RingBuffer进行游戏撮合，撮合算法描述如下：
					 * 1、按照查找
					 * 
					 */
					gameRoom = (GameRoom) CacheHelper.getQueneCache(playway).poll() ;
					
					if(gameRoom != null){	
						/**
						 * 修正获取gameroom获取的问题，因为删除房间的时候，为了不损失性能，没有将 队列里的房间信息删除，如果有玩家获取到这个垃圾信息
						 * 则立即进行重新获取房价， 
						 */
						while(CacheHelper.getGameRoomCacheBean().getCacheObject(gameRoom.getId()) == null){
							gameRoom = (GameRoom) CacheHelper.getQueneCache(playway).poll() ;
							if(gameRoom == null){
								break ;
							}
						}
					}
					
					if(gameRoom==null){	//无房间 ， 需要
						gameRoom = this.creatGameRoom(gamePlayway, userid , false) ;
					}else{
						playUser.setPlayerindex(System.currentTimeMillis());//从后往前坐，房主进入以后优先坐在 首位
						needtakequene =  true ;
					}
				}
			}
			if(gameRoom!=null){
				gameRoom.setCurrentnum(0);
				/**
				 * 更新缓存
				 */
				CacheHelper.getGameRoomCacheBean().put(gameRoom.getId(), gameRoom);
				/**
				 * 如果当前房间到达了最大玩家数量，则不再加入到 撮合队列
				 */
				List<PlayUserClient> playerList = CacheHelper.getGamePlayerCacheBean().getCacheObject(gameRoom.getId()) ;
				if(playerList.size() == 0){
					gameEvent.setEvent(BeiMiGameEvent.ENTER.toString());
				}else{	
					gameEvent.setEvent(BeiMiGameEvent.JOIN.toString());
				}
				gameEvent.setGameRoom(gameRoom);
				gameEvent.setRoomid(gameRoom.getId());
				
				boolean inroom = false ;
				for(PlayUserClient user : playerList){
					if(user.getId().equals(userid)){
						inroom = true ; break ;
					}
				}
				if(inroom == false){
					playUser.setPlayerindex(System.currentTimeMillis());
					playUser.setGamestatus(BMDataContext.GameStatusEnum.READY.toString());
					playUser.setPlayertype(BMDataContext.PlayerTypeEnum.NORMAL.toString());
					playerList.add(playUser) ;
					NettyClients.getInstance().joinRoom(userid, gameRoom.getId());
					CacheHelper.getGamePlayerCacheBean().put(gameRoom.getId(), playUser); //将用户加入到 room ， MultiCache
				}
				for(PlayUserClient temp : playerList){
					if(temp.getId().equals(playUser.getId())){
						gameEvent.setIndex(playerList.indexOf(temp)); break ;
					}
				}
				/**
				 * 如果当前房间到达了最大玩家数量，则不再加入到 撮合队列
				 */
				if(playerList.size() < gamePlayway.getPlayers() && needtakequene == true){
					CacheHelper.getQueneCache(gameRoom.getPlayway()).offer(gameRoom);	//未达到最大玩家数量，加入到游戏撮合 队列，继续撮合
				}
				/**
				 *	不管状态如何，玩家一定会加入到这个房间 
				 */
				CacheHelper.getRoomMappingCacheBean().put(userid, gameRoom.getId());
			}
		}
		return gameEvent;
	}
	
	
	/**
	 * 创建新房间 ，需要传入房间的玩法 ， 玩法定义在 系统运营后台，玩法创建后，放入系统缓存 ， 客户端进入房间的时候，传入 玩法ID参数
	 * @param playway
	 * @param userid
	 * @return
	 */
	private  GameRoom creatGameRoom(GamePlayway playway , String userid , boolean cardroom){
		GameRoom gameRoom = new GameRoom() ;
		gameRoom.setCreatetime(new Date());
		gameRoom.setUpdatetime(new Date());
		
		if(playway!=null){
			gameRoom.setPlayway(playway.getId());
			gameRoom.setRoomtype(playway.getRoomtype());
			gameRoom.setPlayers(playway.getPlayers());
		}
		

		gameRoom.setPlayers(playway.getPlayers());
		gameRoom.setCardsnum(playway.getCardsnum());
		
		gameRoom.setCurpalyers(1);
		gameRoom.setCardroom(cardroom);
		
		gameRoom.setStatus(BeiMiGameEnum.CRERATED.toString());
		
		gameRoom.setCardsnum(playway.getCardsnum());
		
		gameRoom.setCurrentnum(0);
		
		gameRoom.setMaster(userid);
		gameRoom.setNumofgames(playway.getNumofgames());   //无限制
		gameRoom.setOrgi(playway.getOrgi());
		
		CacheHelper.getQueneCache(playway.getId()).offer(gameRoom);	//未达到最大玩家数量，加入到游戏撮合 队列，继续撮合
		
		return gameRoom ;
	}
}
