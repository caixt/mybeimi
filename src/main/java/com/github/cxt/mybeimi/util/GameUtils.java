package com.github.cxt.mybeimi.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import com.github.cxt.mybeimi.core.engine.game.BeiMiGame;
import com.github.cxt.mybeimi.core.engine.game.iface.ChessGame;
import com.github.cxt.mybeimi.core.engine.game.impl.DizhuGame;
import com.github.cxt.mybeimi.core.engine.game.model.Playway;
import com.github.cxt.mybeimi.core.engine.game.model.Type;
import com.github.cxt.mybeimi.util.rules.model.Board;
import com.github.cxt.mybeimi.config.web.model.Game;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.web.model.GameConfig;
import com.github.cxt.mybeimi.web.model.GamePlayway;
import com.github.cxt.mybeimi.web.model.GameRoom;
import com.github.cxt.mybeimi.web.model.PlayUser;
import com.github.cxt.mybeimi.web.model.PlayUserClient;

public class GameUtils {
	
	private static Map<String,ChessGame> games = new HashMap<String,ChessGame>();
	static{
		games.put("dizhu", new DizhuGame()) ;
	}
	
	
	/**
	 * 定缺方法，计算最少的牌
	 * @param cards
	 * @return
	 */
	public static int selectColor(byte[] cards){
		Map<Integer, Byte> data = new HashMap<Integer, Byte>();
		for(byte temp : cards){
			int key = temp / 36 ;				//花色
			if(data.get(key) == null){
				data.put(key , (byte)1) ;
			}else{
				data.put(key, (byte)(data.get(key)+1)) ;
			}
		}
		int color = 0 , cardsNum = 0 ;
		if(data.get(0)!=null){
			cardsNum = data.get(0) ;
			if(data.get(1) == null){
				color = 1 ;
			}else{
				if(data.get(1) < cardsNum){
					cardsNum = data.get(1) ;
					color = 1 ;
				}
				if(data.get(2)==null){
					color = 2 ;
				}else{
					if(data.get(2) < cardsNum){
						cardsNum = data.get(2) ;
						color = 2 ;
					}
				}
			}
		}
		return color ;
	}
	/**
	 * 麻将的出牌判断，杠碰吃胡
	 * @param cards
	 * @param card
	 * @param deal	是否抓牌
	 * @return
	 */
	public static Byte getGangCard(byte[] cards){
		Byte card = null ;
		Map<Integer, Byte> data = new HashMap<Integer, Byte>();
		for(byte temp : cards){
			int value = (temp%36) / 4 ;			//牌面值
			int rote = temp / 36 ;				//花色
			int key = value + 9 * rote ;		//
			if(data.get(key) == null){
				data.put(key , (byte)1) ;
			}else{
				data.put(key, (byte)(data.get(key)+1)) ;
			}
			if(data.get(key) == 4){	//自己发牌的时候，需要先判断是否有杠牌
				card = temp ;
				break ;
			}
		}
		
		return card;
	}
	
	public static byte[] reverseCards(byte[] cards) {  
		byte[] target_cards = new byte[cards.length];  
		for (int i = 0; i < cards.length; i++) {  
			// 反转后数组的第一个元素等于源数组的最后一个元素：  
			target_cards[i] = cards[cards.length - i - 1];  
		}  
		return target_cards;  
	}
	
	public static Game getGame(String playway){
		GamePlayway gamePlayway = (GamePlayway) CacheHelper.getSystemCacheBean().getCacheObject(playway) ;
		Game game = null ;
		if(gamePlayway!=null){
			String code = gamePlayway.getCode();
			if(code.equals("dizhu")){
				game = (Game) BMDataContext.getContext().getBean("dizhuGame") ;
			}else if(code.equals("majiang")){
				game = (Game) BMDataContext.getContext().getBean("majiangGame") ;
			}
		}
		return game;
	}
	
	/**
	 * 获取游戏全局配置，后台管理界面上的配置功能
	 * @param orgi
	 * @return
	 */
	public static GameConfig gameConfig(){
		return (GameConfig) CacheHelper.getSystemCacheBean().getCacheObject(BMDataContext.ConfigNames.GAMECONFIG.toString()) ;
	}
	
	/**
	 * 封装Game信息，基于缓存操作
	 * @param gametype
	 * @return
	 */
	public static List<BeiMiGame> games(String gametype){
		BeiMiGame game = (BeiMiGame) CacheHelper.getSystemCacheBean().getCacheObject(gametype);
		for(Type type : game.getTypes()){
			List<Playway> playways = new ArrayList<>();
			List<GamePlayway> gamePlayways = (List<GamePlayway>) CacheHelper.getSystemCacheBean().getCacheObject(type.getId() + "." + BMDataContext.ConfigNames.PLAYWAYCONFIG.toString());
			if(gamePlayways != null){
				for(GamePlayway gamePlayway : gamePlayways){
					Playway playway = new Playway(gamePlayway.getId(), gamePlayway.getName() , gamePlayway.getCode(), gamePlayway.getScore() , gamePlayway.getMincoins(), gamePlayway.getMaxcoins(), gamePlayway.isChangecard() , gamePlayway.isShuffle()) ;
					playway.setLevel(gamePlayway.getTypelevel());
					playway.setSkin(gamePlayway.getTypecolor());
					playways.add(playway);
				}
			}
			type.setPlayways(playways);
		}
		List<BeiMiGame> data = new ArrayList<>();
		data.add(game);
		return data;
	}
	
	/**
	 * 移除GameRoom
	 * @param gameRoom
	 */
	public static boolean removeGameRoom(GameRoom gameRoom){
		return CacheHelper.getQueneCache(gameRoom.getPlayway()).remove(gameRoom);
	}
	
	
	
	/**
	 * 注册用户
	 * @param player
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public static PlayUserClient create(PlayUser player , String playertype){
		PlayUserClient playUserClient = null ;
		if(player!= null){
    		if(StringUtils.isBlank(player.getUsername())){
    			player.setUsername("Guest_"+Base62.encode(UKTools.getUUID()));
    		}
    		player.setPassword("123456");
    		player.setPlayertype(playertype);	//玩家类型
    		player.setCreatetime(new Date());
    		player.setUpdatetime(new Date());
    		player.setLastlogintime(new Date());
    		player.setOrgi(BMDataContext.SYSTEM_ORGI);
    		
			player.setGoldcoins(5000);
			player.setCards(10);
			player.setDiamonds(66);
    		
    		if(!StringUtils.isBlank(player.getId())){
    			playUserClient  = new PlayUserClient(player);
    		}
    	}
		return playUserClient ;
	}
	
	
	/**
	 * 开始游戏，根据玩法创建游戏 对局
	 * @return
	 */
	public static Board playGame(List<PlayUserClient> playUsers , GameRoom gameRoom , String banker , int cardsnum){
		Board board = null ;
		GamePlayway gamePlayWay = (GamePlayway) CacheHelper.getSystemCacheBean().getCacheObject(gameRoom.getPlayway()) ;
		if(gamePlayWay!=null){
			ChessGame chessGame = games.get(gamePlayWay.getCode());
			if(chessGame!=null){
				board = chessGame.process(playUsers, gameRoom, gamePlayWay , banker, cardsnum);
			}
		}
		return board;
	}
}
