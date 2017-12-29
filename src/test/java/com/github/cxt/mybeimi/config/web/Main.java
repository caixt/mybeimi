package com.github.cxt.mybeimi.config.web;

import static org.mockito.Mockito.mock;
import java.util.UUID;
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
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import com.github.cxt.mybeimi.MainApplication;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.util.GameUtils;
import com.github.cxt.mybeimi.util.UKTools;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
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
	private ApplicationContext context;
	@Autowired
	private GameEventHandler gameEventHandler;
	private SocketIOClient socketIOClient;
	
	@Before
	public void before(){
		BMDataContext.setApplicationContext(context);
		socketIOClient = mock(SocketIOClient.class);
		UUID uuid = UUID.randomUUID();
		Mockito.when(socketIOClient.getSessionId()).thenReturn(uuid);
		Mockito.doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				JSONArray array = (JSONArray) JSONArray.toJSON(invocation.getArguments());
				logger.info("array:" + array.toString());
				return null;
			}
			
			
		}).when(socketIOClient).sendEvent(Mockito.anyString(), Mockito.anyVararg());
		
	}
	
	
	
	@Test
	public void test() throws InterruptedException{
		String tokeId = UKTools.getUUID();
		PlayUserClient user = GameUtils.create(new PlayUser(), BMDataContext.PlayerTypeEnum.NORMAL.toString());
		user.setToken(tokeId);
		
		Token userToken = new Token();
		userToken.setId(tokeId);
		userToken.setUserid(user.getId());
		
		CacheHelper.getInstance().getApiUserCacheBean().put(userToken.getId(), userToken);
		CacheHelper.getInstance().getApiUserCacheBean().put(userToken.getUserid(), user);
		
		BeiMiClient beiMiClient = new BeiMiClient();
		beiMiClient.setToken(userToken.getId());
		beiMiClient.setPlayway("gameplayway01");
		beiMiClient.setOrgi(BMDataContext.SYSTEM_ORGI);
		
		gameEventHandler.onJoinRoom(socketIOClient, null, JSONObject.toJSON(beiMiClient).toString());
		Thread.sleep(1000 * 60 * 60);
	}
}