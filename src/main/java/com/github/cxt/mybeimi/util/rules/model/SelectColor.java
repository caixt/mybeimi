package com.github.cxt.mybeimi.util.rules.model;

import com.github.cxt.mybeimi.core.engine.game.Message;

/**
 * 当前出牌信息
 * 出牌人
 * 牌
 * @author zhangtianyi
 *
 */
public class SelectColor implements Message {
	
	private String banker ;
	private String userid ;
	private int color  = 10;
	private long time ;
	
	
	public SelectColor(String banker){
		this.banker = banker ;
	}
	public SelectColor(String banker , String userid){
		this.userid = userid ;
		this.banker = banker ;
	}
	
	private String command ;
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}


	public String getBanker() {
		return banker;
	}

	public void setBanker(String banker) {
		this.banker = banker;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
