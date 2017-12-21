package com.github.cxt.mybeimi.web.model;

import java.util.Date;
import java.util.UUID;
import com.github.cxt.mybeimi.core.engine.game.Message;


/**
 * @author jaddy0302 Rivulet User.java 2010-3-17
 * 
 */
public class PlayUserClient implements  Message {
	private String id = UUID.randomUUID().toString();
	
	private String username ;
	
	private String orgi ;
	private String creater;
	private Date createtime = new Date();
	private Date updatetime = new Date();
	private Date passupdatetime = new Date();
	
	private long playerindex ;
	
	private String command ;	//指令
	
	private String memo;
	private String city ;	//城市
	private String province ;//省份
	private boolean login ;		//是否登录
	private boolean online ; 	//是否在线
	private String status ;		//
	private boolean datastatus ;//数据状态，是否已删除	
	private boolean headimg ; 	//是否上传头像
	
	private String gamestatus ;	//玩家在游戏中的状态 ： READY : NOTREADY : PLAYING ：MANAGED/托管
	
	private String playertype ;	//玩家类型 ： 玩家：托管玩家，AI
	
	private String token ;
	
	private String playerlevel ;//玩家级别 ， 等级
	private int experience  ;	//玩家经验
	
	
	private String openid ;	//微信
	private String qqid ;
	
	
	private Date lastlogintime = new Date();	//最后登录时间
	
	private int fans ;			//粉丝
	private int follows ;		//关注
	private int integral ;		//积分
	
	private int cards;			//房卡数量
	private int goldcoins;		//金币数量
	private int diamonds ;		//钻石数量
	
	/**
	 *对金币+房卡+id进行RSA签名 ， 任何对ID,cards ， goldcoins 进行修改之前，都需要做签名验证，
	 *签名验证通过后才能进行修改，修改之后，重新签名 
	 */
	private String sign ;	

	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getOrgi() {
		return orgi;
	}


	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}


	public String getCreater() {
		return creater;
	}


	public void setCreater(String creater) {
		this.creater = creater;
	}


	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}


	public Date getUpdatetime() {
		return updatetime;
	}


	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}


	public Date getPassupdatetime() {
		return passupdatetime;
	}


	public void setPassupdatetime(Date passupdatetime) {
		this.passupdatetime = passupdatetime;
	}


	public String getMemo() {
		return memo;
	}


	public void setMemo(String memo) {
		this.memo = memo;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getProvince() {
		return province;
	}


	public void setProvince(String province) {
		this.province = province;
	}


	public boolean isLogin() {
		return login;
	}


	public void setLogin(boolean login) {
		this.login = login;
	}


	public boolean isOnline() {
		return online;
	}


	public void setOnline(boolean online) {
		this.online = online;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public boolean isDatastatus() {
		return datastatus;
	}


	public void setDatastatus(boolean datastatus) {
		this.datastatus = datastatus;
	}


	public boolean isHeadimg() {
		return headimg;
	}


	public void setHeadimg(boolean headimg) {
		this.headimg = headimg;
	}


	public String getPlayerlevel() {
		return playerlevel;
	}


	public void setPlayerlevel(String playerlevel) {
		this.playerlevel = playerlevel;
	}


	public int getExperience() {
		return experience;
	}


	public void setExperience(int experience) {
		this.experience = experience;
	}


	public Date getLastlogintime() {
		return lastlogintime;
	}


	public void setLastlogintime(Date lastlogintime) {
		this.lastlogintime = lastlogintime;
	}


	public int getFans() {
		return fans;
	}


	public void setFans(int fans) {
		this.fans = fans;
	}


	public int getFollows() {
		return follows;
	}


	public void setFollows(int follows) {
		this.follows = follows;
	}


	public int getIntegral() {
		return integral;
	}


	public void setIntegral(int integral) {
		this.integral = integral;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public int getCards() {
		return cards;
	}


	public void setCards(int cards) {
		this.cards = cards;
	}


	public int getGoldcoins() {
		return goldcoins;
	}


	public void setGoldcoins(int goldcoins) {
		this.goldcoins = goldcoins;
	}


	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}


	public int getDiamonds() {
		return diamonds;
	}


	public void setDiamonds(int diamonds) {
		this.diamonds = diamonds;
	}


	public String getOpenid() {
		return openid;
	}


	public void setOpenid(String openid) {
		this.openid = openid;
	}


	public String getQqid() {
		return qqid;
	}


	public void setQqid(String qqid) {
		this.qqid = qqid;
	}


	public String getPlayertype() {
		return playertype;
	}


	public void setPlayertype(String playertype) {
		this.playertype = playertype;
	}

	public long getPlayerindex() {
		return playerindex;
	}


	public void setPlayerindex(long playerindex) {
		this.playerindex = playerindex;
	}

	public String getCommand() {
		return command;
	}


	public void setCommand(String command) {
		this.command = command;
	}


	public String getGamestatus() {
		return gamestatus;
	}


	public void setGamestatus(String gamestatus) {
		this.gamestatus = gamestatus;
	}
}
