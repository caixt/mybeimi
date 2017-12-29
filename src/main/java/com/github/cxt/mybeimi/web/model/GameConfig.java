package com.github.cxt.mybeimi.web.model;

import java.util.Date;

public class GameConfig {

	private String id ;
	private String orgi ;
	private Date createtime = new Date() ;
	private String creater ;
	private String username ;
	private String name ;
	
	private String gametype ;	//玩家默认进入的游戏类型
	
	private int maxuser = 10 ;	
	
	private int initmaxuser = 10 ;	
	
	private String sessionmsg ;	
	private String distribution ;
	private boolean lastagent;	
	private boolean sessiontimeout;	
	private int timeout = 120;		
	private String timeoutmsg ;		
	private boolean resessiontimeout;
	private int retimeout = 120;	
	private String retimeoutmsg ;	
	private boolean satisfaction ;	
	
	private boolean agentreplaytimeout ;	
	private int agenttimeout;
	private String agenttimeoutmsg ;
	
	private boolean hourcheck ;		
	private String workinghours ;	
	private String notinwhmsg ;		
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSessionmsg() {
		return sessionmsg;
	}
	public void setSessionmsg(String sessionmsg) {
		this.sessionmsg = sessionmsg;
	}
	public String getDistribution() {
		return distribution;
	}
	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
	public boolean isLastagent() {
		return lastagent;
	}
	public void setLastagent(boolean lastagent) {
		this.lastagent = lastagent;
	}
	public boolean isSessiontimeout() {
		return sessiontimeout;
	}
	public void setSessiontimeout(boolean sessiontimeout) {
		this.sessiontimeout = sessiontimeout;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getTimeoutmsg() {
		return timeoutmsg;
	}
	public void setTimeoutmsg(String timeoutmsg) {
		this.timeoutmsg = timeoutmsg;
	}
	public boolean isResessiontimeout() {
		return resessiontimeout;
	}
	public void setResessiontimeout(boolean resessiontimeout) {
		this.resessiontimeout = resessiontimeout;
	}
	public int getRetimeout() {
		return retimeout;
	}
	public void setRetimeout(int retimeout) {
		this.retimeout = retimeout;
	}
	public String getRetimeoutmsg() {
		return retimeoutmsg;
	}
	public void setRetimeoutmsg(String retimeoutmsg) {
		this.retimeoutmsg = retimeoutmsg;
	}
	public boolean isSatisfaction() {
		return satisfaction;
	}
	public void setSatisfaction(boolean satisfaction) {
		this.satisfaction = satisfaction;
	}
	public boolean isAgentreplaytimeout() {
		return agentreplaytimeout;
	}
	public void setAgentreplaytimeout(boolean agentreplaytimeout) {
		this.agentreplaytimeout = agentreplaytimeout;
	}
	public int getAgenttimeout() {
		return agenttimeout;
	}
	public void setAgenttimeout(int agenttimeout) {
		this.agenttimeout = agenttimeout;
	}
	public String getAgenttimeoutmsg() {
		return agenttimeoutmsg;
	}
	public void setAgenttimeoutmsg(String agenttimeoutmsg) {
		this.agenttimeoutmsg = agenttimeoutmsg;
	}
	public int getMaxuser() {
		return maxuser;
	}
	public void setMaxuser(int maxuser) {
		this.maxuser = maxuser;
	}
	public int getInitmaxuser() {
		return initmaxuser;
	}
	public void setInitmaxuser(int initmaxuser) {
		this.initmaxuser = initmaxuser;
	}
	public String getWorkinghours() {
		return workinghours;
	}
	public void setWorkinghours(String workinghours) {
		this.workinghours = workinghours;
	}
	public String getNotinwhmsg() {
		return notinwhmsg;
	}
	public void setNotinwhmsg(String notinwhmsg) {
		this.notinwhmsg = notinwhmsg;
	}
	public boolean isHourcheck() {
		return hourcheck;
	}
	public void setHourcheck(boolean hourcheck) {
		this.hourcheck = hourcheck;
	}
	public String getGametype() {
		return gametype;
	}
	public void setGametype(String gametype) {
		this.gametype = gametype;
	}
}
