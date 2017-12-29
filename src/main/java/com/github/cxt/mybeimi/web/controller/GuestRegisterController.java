package com.github.cxt.mybeimi.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.util.GameUtils;
import com.github.cxt.mybeimi.util.UKTools;
import com.github.cxt.mybeimi.util.cache.CacheHelper;
import com.github.cxt.mybeimi.web.model.PlayUser;
import com.github.cxt.mybeimi.web.model.PlayUserClient;
import com.github.cxt.mybeimi.web.model.ResultData;
import com.github.cxt.mybeimi.web.model.Token;


@Controller
@RequestMapping("/api/guest")
public class GuestRegisterController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@RequestMapping
    public ResponseEntity<ResultData> guest(HttpServletRequest request , @Valid String token) {
		String tokeId = UKTools.getUUID();
		PlayUserClient client = createPlayUserClient();
		client.setToken(tokeId);
		
		Token userToken = new Token();
		userToken.setId(tokeId);
		userToken.setUserid(client.getId());
		
		ResultData playerResultData = new ResultData(true, "user_register_success", client, userToken);
//		List<BeiMiGame> games = BeiMiGameData.allGames();
		playerResultData.setGametype(GameUtils.gameConfig().getGametype());
		if(!StringUtils.isBlank(playerResultData.getGametype())){
			playerResultData.setGames(GameUtils.games(playerResultData.getGametype()));
		}
		
		CacheHelper.getApiUserCacheBean().put(userToken.getId(), userToken);
		CacheHelper.getApiUserCacheBean().put(userToken.getUserid(), client);
		log.info("tokenId:{},userId:{}", userToken.getId(), userToken.getUserid());
		return new ResponseEntity<>(playerResultData, HttpStatus.OK);
	}
	
	
	private static PlayUserClient createPlayUserClient(){
		return GameUtils.create(new PlayUser(), BMDataContext.PlayerTypeEnum.NORMAL.toString());
	}
}
