package com.github.cxt.mybeimi.core.engine.game.impl;

import com.github.cxt.mybeimi.core.engine.game.Message;

public class Banker implements Message{

	private String command ;
	private String userid ;
	
	public Banker(String userid){
		this.userid = userid ;
	}
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
}
