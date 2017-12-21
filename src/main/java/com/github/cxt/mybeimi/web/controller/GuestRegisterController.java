package com.github.cxt.mybeimi.web.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGame;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameData;
import com.github.cxt.mybeimi.util.Base62;
import com.github.cxt.mybeimi.util.cache.CacheBean;
import com.github.cxt.mybeimi.web.model.PlayUserClient;
import com.github.cxt.mybeimi.web.model.ResultData;
import com.github.cxt.mybeimi.web.model.Token;


@Controller
@RequestMapping("/api/guest")
public class GuestRegisterController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CacheBean apiUserCache;
	
	@RequestMapping
    public ResponseEntity<ResultData> guest(HttpServletRequest request , @Valid String token) {
		String tokeId = UUID.randomUUID().toString().replace("-", "");
		PlayUserClient client = createPlayUserClient();
		client.setToken(tokeId);
		
		Token userToken = new Token();
		userToken.setId(tokeId);
		userToken.setUserid(client.getId());
		
		ResultData playerResultData = new ResultData(true, "user_register_success", client, userToken);
		List<BeiMiGame> games = BeiMiGameData.allGames();
		playerResultData.setGametype(StringUtils.join(games.stream().map(e -> e.getId()).collect(Collectors.toList()).toArray(new String[]{}), ","));
		playerResultData.setGames(games);
		
		apiUserCache.put(userToken.getId(), userToken);
		apiUserCache.put(userToken.getUserid(), client);
		log.info("tokenId:{},userId:{}", userToken.getId(), userToken.getUserid());
		return new ResponseEntity<>(playerResultData, HttpStatus.OK);
	}
	
	
	private static PlayUserClient createPlayUserClient(){
		PlayUserClient player = new PlayUserClient();
		player.setUsername("Guest_"+Base62.encode(UUID.randomUUID().toString().replace("-", "")));
		player.setPlayertype(BMDataContext.PlayerTypeEnum.NORMAL.toString());	//玩家类型
		player.setCards(23);
		player.setGoldcoins(543210);
		player.setCreatetime(new Date());
		player.setUpdatetime(new Date());
		player.setLastlogintime(new Date());
		player.setOrgi(BMDataContext.SYSTEM_ORGI);
		return player ;
	}
}
