package com.github.cxt.mybeimi.web.model;

import java.util.Date;
import com.github.cxt.mybeimi.util.UKTools;

public class GameRoom {
	
	private String id = UKTools.getUUID();
	private String name ;
	private String code ;
	
	private boolean matchmodel ;	//是否比赛房间
	private String matchid ;//赛事ID
	private int matchscreen;//比赛场次
	private String matchtype;	//比赛类型
	
	private String lastwinner ;	//最后赢的人 ， 可多人 ， 逗号隔开
	
	
	private Date createtime ;
	private String parentid ;
	private String typeid ;
	private String creater;
	private String username ;
	
	private String status ;	//当前状态
	
	private Date updatetime ;
	private String orgi ;
	private String area ;
	
	private String game ;	//游戏类型 ： 麻将：地主：德州
	private int players ;	//最大游戏人数
	private int cardsnum ;	//发牌数量
	private int curpalyers ;	//当前人数
	
	private boolean cardroom ;	//是否房卡模式 
	
	private String master ;	//房主 ，开设房间的人 或第一个进入的人
	
	private String roomtype ;	//房间类型， 房卡：大厅
	
	private String playway ;	//玩法
	
	private int numofgames ;	//局数
	private int currentnum ;	//已完局数
	
	private PlayUser masterUser ;	//房间的创建人
	private GamePlayway gamePlayway ;	//房间玩法
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
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
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getGame() {
		return game;
	}
	public void setGame(String game) {
		this.game = game;
	}
	public int getPlayers() {
		return players;
	}
	public void setPlayers(int players) {
		this.players = players;
	}
	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public String getRoomtype() {
		return roomtype;
	}
	public void setRoomtype(String roomtype) {
		this.roomtype = roomtype;
	}
	public String getPlayway() {
		return playway;
	}
	public void setPlayway(String playway) {
		this.playway = playway;
	}
	public int getNumofgames() {
		return numofgames;
	}
	public void setNumofgames(int numofgames) {
		this.numofgames = numofgames;
	}
	public int getCurrentnum() {
		return currentnum;
	}
	public void setCurrentnum(int currentnum) {
		this.currentnum = currentnum;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getCurpalyers() {
		return curpalyers;
	}
	public void setCurpalyers(int curpalyers) {
		this.curpalyers = curpalyers;
	}
	public boolean isCardroom() {
		return cardroom;
	}
	public void setCardroom(boolean cardroom) {
		this.cardroom = cardroom;
	}
	public PlayUser getMasterUser() {
		return masterUser;
	}
	public void setMasterUser(PlayUser masterUser) {
		this.masterUser = masterUser;
	}
	public GamePlayway getGamePlayway() {
		return gamePlayway;
	}
	public void setGamePlayway(GamePlayway gamePlayway) {
		this.gamePlayway = gamePlayway;
	}
	public boolean isMatchmodel() {
		return matchmodel;
	}
	public void setMatchmodel(boolean matchmodel) {
		this.matchmodel = matchmodel;
	}
	public String getMatchid() {
		return matchid;
	}
	public void setMatchid(String matchid) {
		this.matchid = matchid;
	}
	public int getMatchscreen() {
		return matchscreen;
	}
	public void setMatchscreen(int matchscreen) {
		this.matchscreen = matchscreen;
	}
	public String getMatchtype() {
		return matchtype;
	}
	public void setMatchtype(String matchtype) {
		this.matchtype = matchtype;
	}
	public int getCardsnum() {
		return cardsnum;
	}
	public void setCardsnum(int cardsnum) {
		this.cardsnum = cardsnum;
	}
	public String getLastwinner() {
		return lastwinner;
	}
	public void setLastwinner(String lastwinner) {
		this.lastwinner = lastwinner;
	}
}
