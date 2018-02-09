package com.github.cxt.mybeimi.config.web;

import static org.mockito.Mockito.mock;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import com.github.cxt.mybeimi.MainApplication;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.GameBoard;
import com.github.cxt.mybeimi.core.engine.game.impl.Banker;
import com.github.cxt.mybeimi.core.engine.game.impl.UserBoard;
import com.github.cxt.mybeimi.core.engine.game.model.Summary;
import com.github.cxt.mybeimi.core.engine.game.model.SummaryPlayer;
import com.github.cxt.mybeimi.util.GameUtils;
import com.github.cxt.mybeimi.util.UKTools;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.util.rules.model.GamePlayers;
import com.github.cxt.mybeimi.util.rules.model.JoinRoom;
import com.github.cxt.mybeimi.util.rules.model.TakeDiZhuCards;
import com.github.cxt.mybeimi.util.server.handler.BeiMiClient;
import com.github.cxt.mybeimi.util.server.handler.GameEventHandler;
import com.github.cxt.mybeimi.web.model.PlayUser;
import com.github.cxt.mybeimi.web.model.PlayUserClient;
import com.github.cxt.mybeimi.web.model.Token;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class Main {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private GameEventHandler gameEventHandler;
	private SocketIOClient socketIOClient;
	
	private PlayUserClient user = null;
	private Token userToken = null;
	private static String playway = "gameplayway01";
	
	private CountDownLatch countDown = null;
	private BlockingQueue<Object[]> queue = new ArrayBlockingQueue<>(5);
	
	@Before
	public void before(){
		socketIOClient = mock(SocketIOClient.class);
		UUID uuid = UUID.randomUUID();
		Mockito.when(socketIOClient.getSessionId()).thenReturn(uuid);
		Mockito.doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				logger.info(" " + args[1].getClass());
				JSONArray array = (JSONArray) JSONArray.toJSON(args);
				logger.info("array:" + array.toString());
				queue.put(args);
				return null;
			}
			
			
		}).when(socketIOClient).sendEvent(Mockito.anyString(), Mockito.anyVararg());

		user = GameUtils.create(new PlayUser(), BMDataContext.PlayerTypeEnum.NORMAL.toString());
		String tokeId = UKTools.getUUID();
		user.setToken(tokeId);
		userToken = new Token();
		userToken.setId(tokeId);
		userToken.setUserid(user.getId());
		CacheHelper.getInstance().getApiUserCacheBean().put(userToken.getId(), userToken);
		CacheHelper.getInstance().getApiUserCacheBean().put(userToken.getUserid(), user);
		new Thread(new UserTask()).start();
	}
	
	
	
	@Test
	public void test() throws InterruptedException{
		countDown = new CountDownLatch(1);
		queue.put(new Object[]{"start"});
		try {
			countDown.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("结束了");
	}
	
	
	private class UserTask implements Runnable{
		
		@Override
		public void run() {
			try {
				while(true){
					work(queue.take());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				countDown.countDown();
			}
		}
		
		public void work(Object[] args){
			String commond = (String) args[0];
			if(commond.equals("start")){
				start();
				return ;
			}
			Object obj = args[1];
			if(obj == null){
				return ;
			}
			if(obj instanceof JoinRoom || obj instanceof GamePlayers || obj instanceof Banker || obj instanceof UserBoard){
				doNothing();
			}
			else if (obj instanceof GameBoard){
				snatchLandlord((GameBoard) obj);
			}
			else if (obj instanceof TakeDiZhuCards){
				takeCards((TakeDiZhuCards) obj);
			}
			else if (obj instanceof Summary){
				summary((Summary) obj);
			}
			else {
				logger.warn("not know class {}", obj.getClass());
			}
		}

		private void start(){
			BeiMiClient beiMiClient = new BeiMiClient();
			beiMiClient.setToken(userToken.getId());
			beiMiClient.setPlayway(playway);
			beiMiClient.setOrgi(BMDataContext.SYSTEM_ORGI);
			gameEventHandler.onJoinRoom(socketIOClient, null, JSONObject.toJSON(beiMiClient).toString());
		}
		
		private String getMyUserId(){
			return user.getId();
		}
		
		private void doNothing(){
			
		}
		
		private void snatchLandlord(GameBoard board){
			String commond = board.getCommand();
			String userId = getMyUserId();
			if(StringUtils.equals("catch", commond)){
				if(!StringUtils.equals(userId, board.getUserid())){
					return ;
				}
				if(ThreadLocalRandom.current().nextBoolean()){
					logger.info("我抢地主");
					gameEventHandler.onCatch(socketIOClient, null);
				}
				else {
					logger.info("我不抢地主");
					gameEventHandler.onGiveup(socketIOClient, null);
				}
				
			}
			else if(StringUtils.equals("lasthands", commond)){
				if(StringUtils.equals(board.getUserid(), userId)){
					logger.info("我是地主");
					paly();
				}
				else{
					logger.info("我不是地主");
				}
			}
		}
		
		private void takeCards(TakeDiZhuCards takeCards){
			if(takeCards.isOver()){
				logger.info("该局结束");
				return ;
			}
			String userId = getMyUserId();
			if(StringUtils.equals(userId, takeCards.getNextplayer())){
				paly();
			}
		}
		
		private void summary(Summary summary){
			String userId = getMyUserId();
			for(SummaryPlayer player : summary.getPlayers()){
				if(StringUtils.equals(player.getUserid(), userId)){
					logger.info("我是地主:{},我结果赢了?:{}", player.isDizhu(), player.isWin());
				}
			}
			countDown.countDown();
		}
		
		private void paly(){
			logger.info("轮到我打牌");
		}
	}
}