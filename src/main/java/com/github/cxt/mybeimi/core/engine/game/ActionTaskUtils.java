package com.github.cxt.mybeimi.core.engine.game;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.Message;
import com.github.cxt.mybeimi.core.engine.game.task.dizhu.CreateAutoTask;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.util.client.NettyClients;
import com.github.cxt.mybeimi.util.rules.model.Board;
import com.github.cxt.mybeimi.util.rules.model.DuZhuBoard;
import com.github.cxt.mybeimi.util.rules.model.GamePlayers;
import com.github.cxt.mybeimi.util.rules.model.Player;
import com.github.cxt.mybeimi.util.server.handler.BeiMiClient;
import com.github.cxt.mybeimi.web.model.GameRoom;
import com.github.cxt.mybeimi.web.model.PlayUserClient;


public class ActionTaskUtils {
	
	/**
	 * 
	 * @param times
	 * @param gameRoom
	 * @return
	 */
	public static BeiMiGameTask createAutoTask(int times , GameRoom gameRoom){
		return new CreateAutoTask(times , gameRoom) ;
	}
	/**
	 * 临时放这里，重构的时候 放到 游戏类型的 实现类里
	 * @param board
	 * @param player
	 * @return
	 */
	public static DuZhuBoard doCatch(DuZhuBoard board, Player player , boolean result){
		player.setAccept(result); //抢地主
		player.setDocatch(true);
		board.setDocatch(true);
		if(result){	//抢了地主
			if(board.isAdded() == false){
				board.setAdded(true);
			}else{
				board.setRatio(board.getRatio()*2);
			}
			board.setBanker(player.getPlayuser());
		}
		return board ;
	}
	
	public static PlayUserClient getPlayUserClient(String roomid,String player){
		PlayUserClient playUserClient = null;
		List<PlayUserClient> players = CacheHelper.getGamePlayerCacheBean().getCacheObject(roomid) ;
		for(PlayUserClient user : players){
			if(player.equals(user.getId())){
				playUserClient = user ;
			}
		}
		return playUserClient;
	}
	/**
	 * 临时放这里，重构的时候 放到 游戏类型的 实现类里
	 * @param board
	 * @param player
	 * @return
	 */
	public static void doBomb(Board board , boolean add){
		if(add){	//抢了地主
			board.setRatio(board.getRatio()*2);
		}
	}
	/**
	 * 校验当前出牌是否合规
	 * @param playCardType
	 * @param lastCardType
	 * @return
	 */
	public static boolean allow(CardType playCardType , CardType lastCardType){
		boolean allow = false ;
		if(playCardType.isKing()){	//王炸，无敌
			allow = true ;
		}else if(playCardType.isBomb()){
			if(lastCardType.isBomb()){ //都是炸弹
				if(playCardType.getMaxcard() > lastCardType.getMaxcard()){
					allow = true ;
				}
			}else if(lastCardType.isKing()){
				allow = false ;
			}else{
				allow = true ;
			}
		}else if(lastCardType.isBomb()){	//最后一手牌是炸弹 ， 当前出牌不是炸弹
			allow = false ;
		}else if(playCardType.getCardtype() == lastCardType.getCardtype() && lastCardType.getCardtype() > 0){
			if(playCardType.getCardnum() == lastCardType.getCardnum() && playCardType.getMaxcard() > lastCardType.getMaxcard()){
				allow = true ;
			}
			else if(playCardType.getCardnum() == 1 && playCardType.getMaxcardvalue() == 53){
				allow = true;
			}
		}
		return allow ;
	}
	/**
	 * 分类
	 * @param cards
	 * @return
	 */
	public static Map<Integer , Integer> type(byte[] cards){
		Map<Integer,Integer> types = new HashMap<Integer,Integer>();
		for(int i=0 ; i<cards.length ; i++){
			int card = cards[i]/4 ;
			if(types.get(card) == null){
				types.put(card, 1) ;
			}else{
				types.put(card, types.get(card)+1) ;
			}
		}
		return types ;
	}
	/**
	 * 牌型识别 默认 从大到笑排序
	 * @param cards
	 * @return
	 */
	public static CardType identification(byte[] cards){
		//17 20
		int cardSize = cards.length;
		CardType cardTypeBean = new CardType();
		Map<Integer,Integer> types = new HashMap<Integer,Integer>();
		int max = -1 , maxcard = -1 , mincard = -1;
		for(int i=0 ; i<cards.length ; i++){
			int card = cards[i]/4 ;
			if(types.get(card) == null){
				types.put(card, 1) ;
			}else{
				types.put(card, types.get(card)+1) ;
			}
			if(types.get(card) > max){
				max = types.get(card) ;
				maxcard = card ;
			}
			if(types.get(card) == max){
				if(mincard < 0 || mincard > card){
					mincard = card ;
				}
			}
			
			if(cards[i] > cardTypeBean.getMaxcardvalue()){
				cardTypeBean.setMaxcardvalue(cards[i]);
			}
		}
		int min = 100;
		Iterator<Integer> iterator = types.keySet().iterator() ;
		while(iterator.hasNext()){
			Integer key = iterator.next() ;
			if(types.get(key) < min){
				min = types.get(key) ;
			}
		}
		
		cardTypeBean.setCardnum(cardSize);
		cardTypeBean.setMincard(mincard);
		cardTypeBean.setTypesize(types.size());
		cardTypeBean.setMaxcard(maxcard);
		
		int cardtype = 0;
		switch(types.size()){
			case 1 : 
				switch(max){
					case 1 : cardtype = BMDataContext.CardsTypeEnum.ONE.getType() ;break;		//单张
					case 2 : 
						if(mincard > 12){
							cardtype = BMDataContext.CardsTypeEnum.EIGHT.getType() ; //王炸
						}
						else{
							cardtype = BMDataContext.CardsTypeEnum.TWO.getType() ; 	//一对
						}
						break;	
					case 3 : cardtype = BMDataContext.CardsTypeEnum.THREE.getType() ;break;		//三张
					case 4 : cardtype = BMDataContext.CardsTypeEnum.FOUR.getType() ;break;		//炸弹
				}
				;break ;
			case 2 : 
				switch(max){
					case 3 :
						if(min == 1){//三带一
							cardtype = BMDataContext.CardsTypeEnum.THREEWING.getType() ;
						}else if(min == 2){//三带一对
							cardtype = BMDataContext.CardsTypeEnum.THREEWINGDOUBLE.getType() ;
						}else if(min == 3 && isAva(types, mincard, maxcard)){
							cardtype = BMDataContext.CardsTypeEnum.SEVEN.getType() ; //333444
						}
						break;	
					case 4 : if(min == 2) cardtype = BMDataContext.CardsTypeEnum.FOURWING.getType() ;break;	//444433
				}
				;break ;
			case 3 : 
				switch(max){
					case 2 : if(maxcard < 12 && min == max && isAva(types, mincard, maxcard)){cardtype = BMDataContext.CardsTypeEnum.SIX.getType() ;}break;	//334455
					case 3 : 
						if(min == max && maxcard < 12 && isAva(types, mincard, maxcard)){
							cardtype = BMDataContext.CardsTypeEnum.SEVEN.getType() ; //333444555
						} else if(min == 2 && (maxcard - mincard) == 1 && isAva(types, mincard, maxcard)){
							cardtype = BMDataContext.CardsTypeEnum.SEVENWING.getType() ; //33344466
						}
						break;
					case 4 : 
						if(cardSize == 6 ){
							cardtype = BMDataContext.CardsTypeEnum.FOURWING.getType(); //444435
						}
						else if(cardSize == 8 && min == 2) {
							cardtype = BMDataContext.CardsTypeEnum.FOURWINGDOUBLE.getType() ;//44443355
						}
						break;
						//TODO 33344445
				}
				break;
			case 4 : 
				switch(max){
					case 2 : if(min == 2 && maxcard < 12 && isAva(types, mincard, maxcard)){cardtype = BMDataContext.CardsTypeEnum.SIX.getType() ;}break;		//33445566
					case 3 : 
						if(min == 3){
							if(maxcard < 12 && isAva(types, mincard, maxcard)){
								cardtype = BMDataContext.CardsTypeEnum.SEVEN.getType() ; //33344445556666
							}
							//TODO 333444555888
						}
						else if(cardSize == 10 && min == 2 && maxcard < 12 && isAva(types, mincard, maxcard)){
							cardtype = BMDataContext.CardsTypeEnum.SEVENWINGDOUBLE.getType() ;  //3334445566
						}
						else if(cardSize == 8 && (maxcard - mincard) == 1 && maxcard < 12 && isAva(types, mincard, maxcard)){
							cardtype = BMDataContext.CardsTypeEnum.SEVENWING.getType() ; //33344456
						}
						break;
					case 4 :
						//TODO 3333444455556666当三带1处理
						break;
				};
				break ;
			case 5 : 
				switch(max){
					case 1 : if(isAva(types ,mincard, maxcard)){cardtype = BMDataContext.CardsTypeEnum.FIVE.getType() ;}break;		//34567
					case 2 : if(min == 2 && isAva(types, mincard, maxcard)){cardtype = BMDataContext.CardsTypeEnum.SIX.getType() ;}break;		//3344556677
					case 3 : 
						if(min == 3 && maxcard < 12 && isAva(types, mincard, maxcard)){
							cardtype = BMDataContext.CardsTypeEnum.SEVEN.getType() ; //333444555666777
						}
						else if(cardSize == 12 && maxcard < 12 && (maxcard - mincard) == 2 && isAva(types, mincard, maxcard)){
							cardtype = BMDataContext.CardsTypeEnum.SEVENWING.getType() ;//333444555677
						}
						break;
					case 4 :
						//TODO 33334444555566667777当三带1处理
						break;
				};break ;
			case 6 : 
				switch(max){
					case 1 : if(maxcard < 12 && isAva(types ,mincard, maxcard)){cardtype = BMDataContext.CardsTypeEnum.FIVE.getType() ;}break;		//345678
					case 2 : if(maxcard < 12 && isAva(types ,mincard, maxcard) && max == min){cardtype = BMDataContext.CardsTypeEnum.SIX.getType() ;}break;		//334455667788
					case 3 : if(maxcard < 12 && isAva(types ,mincard, maxcard) && max == min){cardtype = BMDataContext.CardsTypeEnum.SEVEN.getType() ;}break;		//6飞机
				};break ;
			default: 
				switch(max){
				case 1 : if(maxcard < 12 && isAva(types ,mincard, maxcard)){cardtype = BMDataContext.CardsTypeEnum.FIVE.getType() ;}break;		//345678....
				case 2 : if(maxcard < 12 && isAva(types ,mincard, maxcard) && max == min){cardtype = BMDataContext.CardsTypeEnum.SIX.getType() ;}break;		//334455667788..
				};break ;
		}
		cardTypeBean.setCardtype(cardtype);
		cardTypeBean.setKing(cardtype == BMDataContext.CardsTypeEnum.EIGHT.getType());
		cardTypeBean.setBomb(cardtype == BMDataContext.CardsTypeEnum.FOUR.getType());
		return cardTypeBean ;
	}
	
	private static boolean isAva(Map<Integer,Integer> types , int mincard, int maxcard){
		boolean ava = true ;
		int value = types.get(mincard);
		for(int i= mincard ; i <= maxcard; i++){
			if(types.get(i) == null || types.get(i) != value){
				ava = false  ;
			}
		}
		return ava ;
	}
	
	/**
	 * 发送消息给 玩家
	 * @param beiMiClient
	 * @param event
	 * @param gameRoom
	 */
	public static void sendPlayers(BeiMiClient beiMiClient , GameRoom gameRoom){
		beiMiClient.getClient().sendEvent(BMDataContext.BEIMI_MESSAGE_EVENT, new GamePlayers(gameRoom.getPlayers() , 
				CacheHelper.getGamePlayerCacheBean().getCacheObject(gameRoom.getId()), BMDataContext.BEIMI_PLAYERS_EVENT));
	}
	
	public static void sendPlayers(GameRoom gameRoom , List<PlayUserClient> players){
		for(PlayUserClient user : players){
			BeiMiClient client = NettyClients.getInstance().getClient(user.getId()) ;
			if(client!=null){
				client.getClient().sendEvent(BMDataContext.BEIMI_MESSAGE_EVENT, new GamePlayers(gameRoom.getPlayers() , 
						CacheHelper.getGamePlayerCacheBean().getCacheObject(gameRoom.getId()), BMDataContext.BEIMI_PLAYERS_EVENT));
			}
		}
	}
	
	public static void sendEvent(String event, Message message,GameRoom gameRoom){
		message.setCommand(event);
		List<PlayUserClient> players = CacheHelper.getGamePlayerCacheBean().getCacheObject(gameRoom.getId()) ;
		for(PlayUserClient user : players){
			BeiMiClient client = NettyClients.getInstance().getClient(user.getId()) ;
			if(client!=null){
				client.getClient().sendEvent(BMDataContext.BEIMI_MESSAGE_EVENT, message);
			}
		}
	}
	
	public static void sendEvent(PlayUserClient playerUser  , Message message){
		NettyClients.getInstance().sendGameEventMessage(playerUser.getId(), BMDataContext.BEIMI_MESSAGE_EVENT , message);
	}
}
