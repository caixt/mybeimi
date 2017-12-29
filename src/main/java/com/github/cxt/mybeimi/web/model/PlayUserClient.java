package com.github.cxt.mybeimi.web.model;

import org.springframework.beans.BeanUtils;

import com.github.cxt.mybeimi.core.engine.game.Message;



public class PlayUserClient extends PlayUser implements  Message {
	
	private String token ;
	
	private long playerindex ;
	
	private String gamestatus ;	//玩家在游戏中的状态 ： READY : NOTREADY : PLAYING ：MANAGED/托管
	
	private String command ;	//指令
	
	public PlayUserClient(PlayUser user) {
		BeanUtils.copyProperties(user , this);
	}

	public PlayUserClient(PlayUser user, String command) {
		this(user);
		this.command = command;
	}

	@Override
	public String getCommand() {
		return this.command;
	}

	@Override
	public void setCommand(String command) {
		this.command = command;
	}

	public String getGamestatus() {
		return gamestatus;
	}

	public void setGamestatus(String gamestatus) {
		this.gamestatus = gamestatus;
	}

	public long getPlayerindex() {
		return playerindex;
	}

	public void setPlayerindex(long playerindex) {
		this.playerindex = playerindex;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
