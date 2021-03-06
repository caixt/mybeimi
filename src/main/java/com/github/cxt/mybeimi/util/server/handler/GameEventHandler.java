package com.github.cxt.mybeimi.util.server.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.util.client.NettyClients;
import com.github.cxt.mybeimi.util.server.handler.BeiMiClient;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.web.model.PlayUserClient;
import com.github.cxt.mybeimi.web.model.Token;

public class GameEventHandler   {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
    @OnConnect  
    public void onConnect(SocketIOClient client)  
    {  
    	log.info("connect: {}", client.getSessionId());
    }  
    
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client)  
    {  
    	log.info("disconnect: {}", client.getSessionId());
    }
    
    @OnEvent(value = "joinroom")   
    public void onJoinRoom(SocketIOClient client , AckRequest request, String data)  
    {  
    	BeiMiClient beiMiClient = JSON.parseObject(data , BeiMiClient.class) ;
    	String token = beiMiClient.getToken();
		if(!StringUtils.isBlank(token)){
			/**
			 * Token不为空，并且，验证Token有效，验证完毕即开始进行游戏撮合，房卡类型的
			 * 1、大厅房间处理
			 *    a、从房间队列里获取最近一条房间信息
			 *    b、将token对应玩家加入到房间
			 *    c、如果房间凑齐了玩家，则将房间从等待撮合队列中移除，放置到游戏中的房间信息，如果未凑齐玩家，继续扔到队列
			 *    d、通知房间的所有人，有新玩家加入
			 *    e、超时处理，增加AI进入房价
			 *    f、事件驱动
			 *    g、定时器处理
			 * 2、房卡房间处理
			 * 	  a、创建房间
			 * 	  b、加入到等待中队列
			 */
			Token userToken ;
			if(beiMiClient!=null && !StringUtils.isBlank(token) && (userToken = (Token) CacheHelper.getApiUserCacheBean().getCacheObject(token))!=null){
				//鉴权完毕
				PlayUserClient userClient = (PlayUserClient) CacheHelper.getApiUserCacheBean().getCacheObject(userToken.getUserid()) ;
				beiMiClient.setClient(client);
				beiMiClient.setUserid(userClient.getId());
				beiMiClient.setSession(client.getSessionId().toString());
				/**
				 * 心跳时间
				 */
				beiMiClient.setTime(System.currentTimeMillis());
				NettyClients.getInstance().putClient(userClient.getId(), beiMiClient);
				
				/**
				 * 更新当前玩家状态，在线|离线
				 */
				userClient.setOnline(true);
				
				BMDataContext.getGameEngine().gameRequest(userToken.getUserid(), beiMiClient.getPlayway(), beiMiClient.getRoom(), userClient , beiMiClient) ;
			}
		}
    }
    
    
    //抢地主事件
    @OnEvent(value = "docatch")   
    public void onCatch(SocketIOClient client , String data)  
    {  
    	BeiMiClient beiMiClient = NettyClients.getInstance().getClient(client.getSessionId().toString()) ;
    	String token = beiMiClient.getToken();
		if(!StringUtils.isBlank(token)){
			Token userToken = (Token) CacheHelper.getApiUserCacheBean().getCacheObject(token) ;
			if(userToken!=null){
				PlayUserClient playUser = (PlayUserClient) CacheHelper.getApiUserCacheBean().getCacheObject(userToken.getUserid()) ;
				String roomid = (String) CacheHelper.getRoomMappingCacheBean().getCacheObject(playUser.getId()) ;
				BMDataContext.getGameEngine().actionRequest(roomid, playUser, true);
			}
		}
    }
    
    //不抢地主事件
    @OnEvent(value = "giveup")   
    public void onGiveup(SocketIOClient client , String data)  
    {  
    	BeiMiClient beiMiClient = NettyClients.getInstance().getClient(client.getSessionId().toString()) ;
    	String token = beiMiClient.getToken();
		if(!StringUtils.isBlank(token)){
			Token userToken = (Token) CacheHelper.getApiUserCacheBean().getCacheObject(token) ;
			if(userToken!=null){
				PlayUserClient playUser = (PlayUserClient) CacheHelper.getApiUserCacheBean().getCacheObject(userToken.getUserid()) ;
				String roomid = (String) CacheHelper.getRoomMappingCacheBean().getCacheObject(playUser.getId()) ;
				BMDataContext.getGameEngine().actionRequest(roomid, playUser, false);
			}
		}
    }
    
    
    //出牌
    @OnEvent(value = "doplaycards")   
    public void onPlayCards(SocketIOClient client , String data)  
    {  
    	BeiMiClient beiMiClient = NettyClients.getInstance().getClient(client.getSessionId().toString()) ;
    	String token = beiMiClient.getToken();
		if(!StringUtils.isBlank(token) && !StringUtils.isBlank(data)){
			Token userToken = (Token) CacheHelper.getApiUserCacheBean().getCacheObject(token) ;
			if(userToken!=null){
				String roomid = (String) CacheHelper.getRoomMappingCacheBean().getCacheObject(userToken.getUserid()) ;
				String[] cards = data.split(",") ;
				
				byte[] playCards = new byte[cards.length] ;
				for(int i= 0 ; i<cards.length ; i++){
					playCards[i] = Byte.parseByte(cards[i]) ;
				}
				BMDataContext.getGameEngine().takeCardsRequest(roomid, userToken.getUserid(), false , playCards);
			}
		}
    }
    
    //不出牌
    @OnEvent(value = "nocards")   
    public void onNoCards(SocketIOClient client , String data)  
    {  
    	BeiMiClient beiMiClient = NettyClients.getInstance().getClient(client.getSessionId().toString()) ;
    	String token = beiMiClient.getToken();
		if(!StringUtils.isBlank(token)){
			Token userToken = (Token) CacheHelper.getApiUserCacheBean().getCacheObject(token) ;
			if(userToken!=null){
				PlayUserClient playUser = (PlayUserClient) CacheHelper.getApiUserCacheBean().getCacheObject(userToken.getUserid()) ;
				String roomid = (String) CacheHelper.getRoomMappingCacheBean().getCacheObject(playUser.getId()) ;
				BMDataContext.getGameEngine().takeCardsRequest(roomid, userToken.getUserid(), false , null);
			}
		}
    }
    
    //重新开始
    @OnEvent(value = "restart")   
    public void onRestart(SocketIOClient client , String data)  
    {  
    	BeiMiClient beiMiClient = NettyClients.getInstance().getClient(client.getSessionId().toString()) ;
    	String token = beiMiClient.getToken();
		if(!StringUtils.isBlank(token)){
			Token userToken = (Token) CacheHelper.getApiUserCacheBean().getCacheObject(token) ;
			if(userToken!=null){
				PlayUserClient playUser = (PlayUserClient) CacheHelper.getApiUserCacheBean().getCacheObject(userToken.getUserid()) ;
				String roomid = (String) CacheHelper.getRoomMappingCacheBean().getCacheObject(playUser.getId()) ;
				BMDataContext.getGameEngine().restartRequest(roomid, playUser.getId(), beiMiClient);
			}
		}
    }
    
    /*@OnEvent(value = "joinroom")   
    public void onJoinRoom(SocketIOClient client , AckRequest request, String data)  
    {  
    	String str = null;
    	JSONObject json = null;
    	log.info(data);
    	BeiMiClient beiMiClient = JSON.parseObject(data , BeiMiClient.class);
    	PlayUserClient ai1 = createAIPlayUserClient();
    	PlayUserClient ai2 = createAIPlayUserClient();
    	Token token = (Token) apiUserCache.getCacheObject(beiMiClient.getToken());
    	PlayUserClient user =  (PlayUserClient) apiUserCache.getCacheObject(token.getUserid());
    	
    	//加入房间
    	sleep(5);
    	str = "{'command':'joinroom','player':null,'index':0,'maxplayers':3}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("player", user);
    	client.sendEvent("command", json);
    	
    	sleep(1);
    	str = "{'maxplayers':3,'command':'players','player':[]}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.getJSONArray("player").add(user);
    	client.sendEvent("command", json);
    	
    	//加入玩家
    	sleep(1);
    	str = "{'maxplayers':3,'command':'players','player':[]}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.getJSONArray("player").add(user);
    	json.getJSONArray("player").add(ai1);
    	json.getJSONArray("player").add(ai2);
    	client.sendEvent("command", json);
    	
    	sleep(1);
    	str = "{'command':'banker','userid':null}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	client.sendEvent("command", json);
    	
    	//发牌
    	sleep(1);
    	str = "{'player':{'playuser':null,'cards':{},'history':{},'info':0,'randomcard':false,'docatch':false,'recatch':false,'deskcards':0,'selected':false,'color':0,'accept':false,'banker':false,'actions':[]},'players':[{'playuser':null,'history':{},'info':0,'randomcard':true,'docatch':false,'recatch':false,'deskcards':17,'selected':false,'color':0,'accept':false,'banker':false,'actions':[]},{'playuser':null,'history':{},'info':0,'randomcard':false,'docatch':false,'recatch':false,'deskcards':17,'selected':false,'color':0,'accept':false,'banker':false,'actions':[]}],'deskcards':0,'command':'play'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.getJSONObject("player").put("playuser", user.getId());
    	json.getJSONObject("player").put("cards", new byte[]{52,47,46,43,36,30,29,28,26,25,23,19,17,15,13,12,5});
    	json.getJSONArray("players").getJSONObject(0).put("playuser", ai1.getId());
    	json.getJSONArray("players").getJSONObject(1).put("playuser", ai2.getId());
    	client.sendEvent("command", json);
    	
    	//抢地主
    	sleep(3);
    	str = "{'userid':null,'docatch':true,'grab':true,'ratio':15,'command':'catch'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'userid':null,'docatch':true,'grab':true,'ratio':15,'command':'catchresult'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'userid':null,'docatch':true,'grab':true,'ratio':30,'command':'catch'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'userid':null,'docatch':true,'grab':true,'ratio':30,'command':'catchresult'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'userid':null,'docatch':true,'grab':true,'ratio':30,'command':'catch'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'userid':null,'docatch':true,'grab':true,'ratio':60,'command':'catchresult'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'userid':null,'docatch':true,'grab':true,'ratio':120,'command':'catch'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'userid':null,'docatch':true,'grab':true,'ratio':120,'command':'catchresult'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	client.sendEvent("command", json);
    	
    	//发底牌
    	sleep(2);
    	str = "{'lasthands': null,'userid':null,'docatch':false,'grab':false,'ratio':120,'command':'lasthands'}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("lasthands", new byte[]{7,9,3});
    	client.sendEvent("command", json);
    	
    	//打牌1
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'383cc08ddc1043579936e5b43f324072','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':4,'cardType':{'maxcard':0,'cardtype':4,'typesize':2,'king':false,'bomb':false,'mincard':0,'cardnum':3,'maxcardvalue':9},'command':'takecards','sameside':false,'cardsnum':16,'nextplayer':'9c33682dcaab43b3bd096ebf7bff21b1','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("nextplayer", ai2.getId());
    	json.put("cards", new byte[]{0,1,3,9});
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'9c33682dcaab43b3bd096ebf7bff21b1','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':4,'cardType':{'maxcard':2,'cardtype':4,'typesize':2,'king':false,'bomb':false,'mincard':0,'cardnum':3,'maxcardvalue':11},'command':'takecards','sameside':true,'cardsnum':13,'nextplayer':'8f07c8867135462fbc4ff02a38043545','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	json.put("nextplayer", user.getId());
    	json.put("cards", new byte[]{2,8,10,11});
    	client.sendEvent("command", json);
    
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'8f07c8867135462fbc4ff02a38043545','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':4,'cardType':{'maxcard':7,'cardtype':4,'typesize':2,'king':false,'bomb':false,'mincard':1,'cardnum':3,'maxcardvalue':30},'command':'takecards','sameside':true,'cardsnum':13,'nextplayer':'383cc08ddc1043579936e5b43f324072','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	json.put("nextplayer", ai1.getId());
    	json.put("cards", new byte[]{5,28,29,30});
    	client.sendEvent("command", json);
    	
    	//打牌2
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'383cc08ddc1043579936e5b43f324072','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':4,'cardType':{'maxcard':10,'cardtype':4,'typesize':2,'king':false,'bomb':false,'mincard':4,'cardnum':3,'maxcardvalue':42},'command':'takecards','sameside':false,'cardsnum':12,'nextplayer':'9c33682dcaab43b3bd096ebf7bff21b1','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("nextplayer", ai2.getId());
    	json.put("cards", new byte[]{16,40,41,42});
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'9c33682dcaab43b3bd096ebf7bff21b1','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':4,'cardType':{'maxcard':5,'cardtype':4,'typesize':2,'king':false,'bomb':false,'mincard':3,'cardnum':3,'maxcardvalue':22},'command':'takecards','sameside':true,'cardsnum':9,'nextplayer':'8f07c8867135462fbc4ff02a38043545','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	json.put("nextplayer", user.getId());
    	json.put("cards", new byte[]{14,20,21,22});
    	client.sendEvent("command", json);
    
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'8f07c8867135462fbc4ff02a38043545','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':true,'cardsnum':13,'nextplayer':'383cc08ddc1043579936e5b43f324072','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	json.put("nextplayer", ai1.getId());
    	client.sendEvent("command", json);
    	
    	//打牌3
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'383cc08ddc1043579936e5b43f324072','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':4,'cardType':{'maxcard':12,'cardtype':4,'typesize':2,'king':false,'bomb':false,'mincard':8,'cardnum':3,'maxcardvalue':50},'command':'takecards','sameside':false,'cardsnum':8,'nextplayer':'9c33682dcaab43b3bd096ebf7bff21b1','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("nextplayer", ai2.getId());
    	json.put("cards", new byte[]{32,48,49,50});
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'9c33682dcaab43b3bd096ebf7bff21b1','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':false,'cardsnum':9,'nextplayer':'8f07c8867135462fbc4ff02a38043545','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	json.put("nextplayer", user.getId());
    	client.sendEvent("command", json);
    
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'8f07c8867135462fbc4ff02a38043545','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':false,'cardsnum':13,'nextplayer':'383cc08ddc1043579936e5b43f324072','automic':true,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	json.put("nextplayer", ai1.getId());
    	client.sendEvent("command", json);
    	
    	//打牌4
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'383cc08ddc1043579936e5b43f324072','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':4,'cardType':{'maxcard':1,'cardtype':4,'typesize':2,'king':false,'bomb':false,'mincard':1,'cardnum':3,'maxcardvalue':37},'command':'takecards','sameside':false,'cardsnum':4,'nextplayer':'9c33682dcaab43b3bd096ebf7bff21b1','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("nextplayer", ai2.getId());
    	json.put("cards", new byte[]{4,6,7,37});
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'9c33682dcaab43b3bd096ebf7bff21b1','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':4,'cardType':{'maxcard':8,'cardtype':4,'typesize':2,'king':false,'bomb':false,'mincard':4,'cardnum':3,'maxcardvalue':35},'command':'takecards','sameside':true,'cardsnum':5,'nextplayer':'8f07c8867135462fbc4ff02a38043545','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	json.put("nextplayer", user.getId());
    	json.put("cards", new byte[]{18,33,34,35});
    	client.sendEvent("command", json);
    
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'8f07c8867135462fbc4ff02a38043545','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':true,'cardsnum':13,'nextplayer':'383cc08ddc1043579936e5b43f324072','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	json.put("nextplayer", ai1.getId());
    	client.sendEvent("command", json);
    	
    	//打牌5
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'383cc08ddc1043579936e5b43f324072','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':false,'cardsnum':4,'nextplayer':'9c33682dcaab43b3bd096ebf7bff21b1','automic':true,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("nextplayer", ai2.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'9c33682dcaab43b3bd096ebf7bff21b1','cards':{},'card':31,'over':false,'bomb':false,'time':0,'type':1,'cardType':{'maxcard':7,'cardtype':1,'typesize':1,'king':false,'bomb':false,'mincard':7,'cardnum':1,'maxcardvalue':31},'command':'takecards','sameside':true,'cardsnum':4,'nextplayer':'8f07c8867135462fbc4ff02a38043545','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	json.put("nextplayer", user.getId());
    	json.put("cards", new byte[]{31});
    	client.sendEvent("command", json);
    
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'8f07c8867135462fbc4ff02a38043545','cards':{},'card':36,'over':false,'bomb':false,'time':0,'type':1,'cardType':{'maxcard':9,'cardtype':1,'typesize':1,'king':false,'bomb':false,'mincard':9,'cardnum':1,'maxcardvalue':36},'command':'takecards','sameside':true,'cardsnum':12,'nextplayer':'383cc08ddc1043579936e5b43f324072','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	json.put("nextplayer", ai1.getId());
    	json.put("cards", new byte[]{36});
    	client.sendEvent("command", json);
    	
    	//打牌6
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'383cc08ddc1043579936e5b43f324072','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':false,'cardsnum':4,'nextplayer':'9c33682dcaab43b3bd096ebf7bff21b1','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("nextplayer", ai2.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'9c33682dcaab43b3bd096ebf7bff21b1','cards':{},'card':51,'over':false,'bomb':false,'time':0,'type':1,'cardType':{'maxcard':12,'cardtype':1,'typesize':1,'king':false,'bomb':false,'mincard':12,'cardnum':1,'maxcardvalue':51},'command':'takecards','sameside':true,'cardsnum':3,'nextplayer':'8f07c8867135462fbc4ff02a38043545','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	json.put("nextplayer", user.getId());
    	json.put("cards", new byte[]{51});
    	client.sendEvent("command", json);
    
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'8f07c8867135462fbc4ff02a38043545','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':true,'cardsnum':12,'nextplayer':'383cc08ddc1043579936e5b43f324072','automic':false,'nextplayercard':0}";
		json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	json.put("nextplayer", ai1.getId());
    	client.sendEvent("command", json);
    	
    	//打牌7
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'383cc08ddc1043579936e5b43f324072','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':false,'cardsnum':4,'nextplayer':'9c33682dcaab43b3bd096ebf7bff21b1','automic':true,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("nextplayer", ai2.getId());
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'9c33682dcaab43b3bd096ebf7bff21b1','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':2,'cardType':{'maxcard':9,'cardtype':2,'typesize':1,'king':false,'bomb':false,'mincard':9,'cardnum':2,'maxcardvalue':39},'command':'takecards','sameside':true,'cardsnum':1,'nextplayer':'8f07c8867135462fbc4ff02a38043545','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	json.put("nextplayer", user.getId());
    	json.put("cards", new byte[]{38,39});
    	client.sendEvent("command", json);
    
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'8f07c8867135462fbc4ff02a38043545','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':true,'cardsnum':12,'nextplayer':'383cc08ddc1043579936e5b43f324072','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	json.put("nextplayer", ai1.getId());
    	client.sendEvent("command", json);
    	
    	//打牌8
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'383cc08ddc1043579936e5b43f324072','cards':{},'card':0,'over':false,'bomb':false,'time':0,'type':2,'cardType':{'maxcard':11,'cardtype':2,'typesize':1,'king':false,'bomb':false,'mincard':11,'cardnum':2,'maxcardvalue':45},'command':'takecards','sameside':false,'cardsnum':2,'nextplayer':'9c33682dcaab43b3bd096ebf7bff21b1','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("nextplayer", ai2.getId());
    	json.put("cards", new byte[]{44, 45});
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'9c33682dcaab43b3bd096ebf7bff21b1','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':false,'cardsnum':1,'nextplayer':'8f07c8867135462fbc4ff02a38043545','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	json.put("nextplayer", user.getId());
    	client.sendEvent("command", json);
    
    	sleep(2);
    	str = "{'allow':true,'donot':true,'userid':'8f07c8867135462fbc4ff02a38043545','card':0,'over':false,'bomb':false,'time':0,'type':0,'command':'takecards','sameside':false,'cardsnum':12,'nextplayer':'383cc08ddc1043579936e5b43f324072','automic':true,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", user.getId());
    	json.put("nextplayer", ai1.getId());
    	client.sendEvent("command", json);
    
    	//打牌9
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'383cc08ddc1043579936e5b43f324072','cards':{},'card':24,'over':false,'bomb':false,'time':0,'type':1,'cardType':{'maxcard':6,'cardtype':1,'typesize':1,'king':false,'bomb':false,'mincard':6,'cardnum':1,'maxcardvalue':24},'command':'takecards','sameside':false,'cardsnum':1,'nextplayer':'9c33682dcaab43b3bd096ebf7bff21b1','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai1.getId());
    	json.put("nextplayer", ai2.getId());
    	json.put("cards", new byte[]{24});
    	client.sendEvent("command", json);
    	
    	sleep(2);
    	str = "{'allow':true,'donot':false,'userid':'9c33682dcaab43b3bd096ebf7bff21b1','cards':{},'card':53,'over':true,'bomb':false,'time':0,'type':1,'cardType':{'maxcard':13,'cardtype':1,'typesize':1,'king':false,'bomb':false,'mincard':13,'cardnum':1,'maxcardvalue':53},'command':'takecards','sameside':true,'cardsnum':0,'nextplayer':'8f07c8867135462fbc4ff02a38043545','automic':false,'nextplayercard':0}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("userid", ai2.getId());
    	json.put("nextplayer", user.getId());
    	json.put("cards", new byte[]{53});
    	client.sendEvent("command", json);
    	
    	//结束
    	sleep(2);
    	str = "{'game':'bfe1487415164073a2ef9bf1d3f66ee4','ratio':120,'command':'allcards','finished':true,'gameRoomOver':true,'score':1200000,'players':[{'userid':'8f07c8867135462fbc4ff02a38043545','username':'Guest_0Q1goM','ratio':120,'score':2500,'gameover':false,'win':true,'cards':{},'dizhu':false},{'userid':'383cc08ddc1043579936e5b43f324072','username':'Guest_0YxZkA','ratio':120,'score':5000,'gameover':false,'win':false,'cards':{},'dizhu':true},{'userid':'9c33682dcaab43b3bd096ebf7bff21b1','username':'Guest_0VFocw','ratio':120,'score':2500,'gameover':false,'win':true,'cards':{},'dizhu':false}]}";
    	json = JSON.parseObject(str.replace("'", "\""));
    	json.put("game", UUID.randomUUID().toString());
    	json.getJSONArray("players").getJSONObject(0).put("cards", new byte[]{27});
    	json.getJSONArray("players").getJSONObject(0).put("userid", user.getId());
    	json.getJSONArray("players").getJSONObject(1).put("cards", new byte[]{});
    	json.getJSONArray("players").getJSONObject(1).put("userid", ai1.getId());
    	json.getJSONArray("players").getJSONObject(2).put("cards", new byte[]{});
    	json.getJSONArray("players").getJSONObject(2).put("userid", ai2.getId());
    	client.sendEvent("command", json);
    }
    
    
    private static PlayUserClient createAIPlayUserClient(){
    	return GameUtils.create(new PlayUser(), BMDataContext.PlayerTypeEnum.AI.toString());
	}
    
    private static void sleep(int n){
    	try {
			Thread.sleep(n * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }*/
}  