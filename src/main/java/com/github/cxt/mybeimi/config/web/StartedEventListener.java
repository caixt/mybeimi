package com.github.cxt.mybeimi.config.web;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGame;
import com.github.cxt.mybeimi.core.engine.game.GameEngine;
import com.github.cxt.mybeimi.core.engine.game.model.Type;
import com.github.cxt.mybeimi.util.UKTools;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.web.model.GameConfig;
import com.github.cxt.mybeimi.web.model.GamePlayway;


@Component
public class StartedEventListener implements ApplicationListener<ContextRefreshedEvent> {
	
	@Resource
	private GameEngine gameEngine ;
	
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	if(BMDataContext.getContext() == null){
    		BMDataContext.setApplicationContext(event.getApplicationContext());
    	}
    	BMDataContext.setGameEngine(gameEngine);
    	
    	
    	List<GamePlayway> gamePlayways = new ArrayList<>();
    	GamePlayway gamePlayway = null;
    	
    	gamePlayway = new GamePlayway();
    	gamePlayway.setId("gameplayway01");
    	gamePlayway.setName("初级场");
    	gamePlayway.setCode("dizhu");
    	gamePlayway.setScore(10000);
    	gamePlayway.setMincoins(1000);
    	gamePlayway.setMaxcoins(100000);
    	gamePlayway.setChangecard(false);
    	gamePlayway.setShuffle(true);
    	gamePlayway.setTypelevel("1");
    	gamePlayway.setTypecolor("2");
    	gamePlayway.setCardsnum(17);
    	gamePlayway.setPlayers(3);
    	gamePlayway.setShuffletimes(1);
    	CacheHelper.getSystemCacheBean().put(gamePlayway.getId(), gamePlayway);
    	gamePlayways.add(gamePlayway);
    	
    	gamePlayway = new GamePlayway();
    	gamePlayway.setId("gameplayway02");
    	gamePlayway.setName("高级场");
    	gamePlayway.setCode("dizhu");
    	gamePlayway.setScore(50000);
    	gamePlayway.setMincoins(50000);
    	gamePlayway.setMaxcoins(200000);
    	gamePlayway.setChangecard(false);
    	gamePlayway.setShuffle(true);
    	gamePlayway.setTypelevel("2");
    	gamePlayway.setTypecolor("2");
    	gamePlayway.setCardsnum(17);
    	gamePlayway.setPlayers(3);
    	gamePlayway.setShuffletimes(1);
    	CacheHelper.getSystemCacheBean().put(gamePlayway.getId(), gamePlayway);
    	gamePlayways.add(gamePlayway);
    	
    	gamePlayway = new GamePlayway();
    	gamePlayway.setId("gameplayway03");
    	gamePlayway.setName("初级场（不洗牌）");
    	gamePlayway.setCode("dizhu");
    	gamePlayway.setScore(10000);
    	gamePlayway.setMincoins(1000);
    	gamePlayway.setMaxcoins(100000);
    	gamePlayway.setChangecard(false);
    	gamePlayway.setShuffle(false);
    	gamePlayway.setTypelevel("1");
    	gamePlayway.setTypecolor("1");
    	gamePlayway.setCardsnum(17);
    	gamePlayway.setPlayers(3);
    	gamePlayway.setShuffletimes(1);
    	CacheHelper.getSystemCacheBean().put(gamePlayway.getId(), gamePlayway);
    	gamePlayways.add(gamePlayway);
    	
    	gamePlayway = new GamePlayway();
    	gamePlayway.setId("gameplayway04");
    	gamePlayway.setName("高级场（不洗牌）");
    	gamePlayway.setCode("dizhu");
    	gamePlayway.setScore(50000);
    	gamePlayway.setMincoins(50000);
    	gamePlayway.setMaxcoins(200000);
    	gamePlayway.setChangecard(false);
    	gamePlayway.setShuffle(false);
    	gamePlayway.setTypelevel("2");
    	gamePlayway.setTypecolor("2");
    	gamePlayway.setCardsnum(17);
    	gamePlayway.setPlayers(3);
    	gamePlayway.setShuffletimes(1);
    	CacheHelper.getSystemCacheBean().put(gamePlayway.getId(), gamePlayway);
    	gamePlayways.add(gamePlayway);
    	
    	
    	
    	List<Type> types = new ArrayList<>();
		Type type = null;
		type = new Type(UKTools.getUUID(), "经典玩法", "basic");
		CacheHelper.getSystemCacheBean().put(type.getId() + "." + BMDataContext.ConfigNames.PLAYWAYCONFIG.toString() , gamePlayways);
		types.add(type);
		type = new Type(UKTools.getUUID(), "房间模式", "room");
		types.add(type);
    	
    	
    	
    	BeiMiGame beiMiGame = null;
    	beiMiGame = new BeiMiGame();
    	beiMiGame.setName("斗地主");
		beiMiGame.setId("__game__01");
		beiMiGame.setCode("dizhuhall");
		beiMiGame.setTypes(types);
		CacheHelper.getSystemCacheBean().put(beiMiGame.getId(), beiMiGame);
		
		
		GameConfig gameConfig = new GameConfig();
    	gameConfig.setId("gametype01");
    	gameConfig.setGametype(beiMiGame.getId());
    	CacheHelper.getSystemCacheBean().put(BMDataContext.ConfigNames.GAMECONFIG.toString(), gameConfig);
    	
		
		
    	beiMiGame = new BeiMiGame();
    	beiMiGame.setName("麻将");
		beiMiGame.setId("__game__02");
		beiMiGame.setCode("majianghall");
		CacheHelper.getSystemCacheBean().put(beiMiGame.getId(), beiMiGame);
		

    }
}