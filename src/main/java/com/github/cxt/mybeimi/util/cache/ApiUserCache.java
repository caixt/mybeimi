package com.github.cxt.mybeimi.util.cache;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service("api_user_cache")
public class ApiUserCache implements CacheBean{
	
	public Map<String, Object> cache = new HashMap<>();
	
	@Override
	public void put(String key, Object value) {
		cache.put(key, value) ;
	}

	
	@Override
	public Object getCacheObject(String key) {
		return cache.get(key);
	}
}
