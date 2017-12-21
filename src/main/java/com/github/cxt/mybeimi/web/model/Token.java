package com.github.cxt.mybeimi.web.model;

import java.util.Date;

/**
 * @author jaddy0302 Rivulet User.java 2010-3-17
 * 
 */
public class Token {

	/**
	 * 
	 */
	private String id;
	
	private String token ;		//token
	private String userid ;		//user id
	
	private Date exptime ;		//过期时间
	
	private String client ;		//终端类型 ， IOS/ANDROID/H5/OTHER
	
	private String ip ;
	
	
	private String region ;
	
	private String orgi ;
	private String creater;
	private Date createtime = new Date();
	private Date updatetime = new Date();
	private Date passupdatetime = new Date();
	
	private Date lastlogintime = new Date();	//最后登录时间
		
	public String getId() {
		return id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Date getExptime() {
		return exptime;
	}

	public void setExptime(Date exptime) {
		this.exptime = exptime;
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

	public Date getLastlogintime() {
		return lastlogintime;
	}

	public void setLastlogintime(Date lastlogintime) {
		this.lastlogintime = lastlogintime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
}
