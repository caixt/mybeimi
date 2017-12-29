package com.github.cxt.mybeimi.util.cache;


public interface CacheBean {
	/**
	 * 
	 */
	public void put(String key , Object value) ;
	/**
	 * 
	 * @param key
	 * @param orgi
	 * @return
	 */
	public Object getCacheObject(String key) ;
	
	
	public boolean delete(String key) ;
	
}
