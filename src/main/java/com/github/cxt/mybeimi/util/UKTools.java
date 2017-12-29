package com.github.cxt.mybeimi.util;

import java.util.UUID;

public class UKTools {
	
	/**
	 * 当前时间+已过随机生成的 长整形数字
	 * @return
	 */
	public static String genID(){
		return Base62.encode(getUUID()).toLowerCase() ;
	}
	
	public static String genIDByKey(String key){
		return Base62.encode(key).toLowerCase() ;
	}
	
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "") ;
	}
	
	public static String getContextID(String session){
		return session.replaceAll("-", "") ;
	}
	
}
