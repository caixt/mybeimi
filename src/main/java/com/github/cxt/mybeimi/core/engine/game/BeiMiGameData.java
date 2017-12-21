package com.github.cxt.mybeimi.core.engine.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.github.cxt.mybeimi.core.engine.game.model.Playway;
import com.github.cxt.mybeimi.core.engine.game.model.Type;

public class BeiMiGameData {

	public static List<BeiMiGame> allGames(){
		List<BeiMiGame> games = new ArrayList<>();
		BeiMiGame game = null;
		Type type = null;
		List<Playway> playways = null;
		Playway playway = null;
		
		game = new BeiMiGame();
		games.add(game);
		game.setId(UUID.randomUUID().toString());
		game.setName("斗地主");
		game.setCode("dizhuhall");
		List<Type> types = new ArrayList<>();
		game.setTypes(types);
		type = new Type(UUID.randomUUID().toString(), "经典玩法", "basic");
		types.add(type);
		playways = new ArrayList<>();
		type.setPlayways(playways);
		
		playway = new Playway(UUID.randomUUID().toString(), "初级场", "dizhu", 10000, 1000, 100000, false, true);
		playway.setLevel("1");
		playway.setSkin("2");
		playways.add(playway);
		playway = new Playway(UUID.randomUUID().toString(), "初级场（不洗牌）", "dizhu", 10000, 1000, 100000, false, false);
		playway.setLevel("1");
		playway.setSkin("1");
		playways.add(playway);
		playway = new Playway(UUID.randomUUID().toString(), "高级场", "dizhu", 50000, 50000, 200000, false, true);
		playway.setLevel("2");
		playway.setSkin("2");
		playways.add(playway);
		playway = new Playway(UUID.randomUUID().toString(), "高级场（不洗牌）", "dizhu", 50000, 50000, 200000, false, false);
		playway.setLevel("2");
		playway.setSkin("2");
		playways.add(playway);
		
		type = new Type(UUID.randomUUID().toString(), "房间模式", "room");
		types.add(type);
		return games;
	} 
	
}
