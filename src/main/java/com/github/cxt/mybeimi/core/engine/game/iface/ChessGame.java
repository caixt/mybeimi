package com.github.cxt.mybeimi.core.engine.game.iface;

import java.util.List;

import com.github.cxt.mybeimi.util.rules.model.Board;
import com.github.cxt.mybeimi.web.model.GamePlayway;
import com.github.cxt.mybeimi.web.model.GameRoom;
import com.github.cxt.mybeimi.web.model.PlayUserClient;


/**
 * 棋牌游戏接口API
 * @author iceworld
 *
 */
public interface ChessGame {
	/**
	 * 创建一局新游戏
	 * @return
	 */
	public Board process(List<PlayUserClient> playUsers , GameRoom gameRoom ,GamePlayway playway ,  String banker , int cardsnum) ;
}
