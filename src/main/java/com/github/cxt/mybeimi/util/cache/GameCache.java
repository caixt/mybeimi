package com.github.cxt.mybeimi.util.cache;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service("game_cache")
public class GameCache implements CacheBean{
	
	public Map<String, Object> cache = new HashMap<>();
	
	@Override
	public void put(String key, Object value) {
		cache.put(key, value) ;
	}

	
	@Override
	public Object getCacheObject(String key) {
		return cache.get(key);
	}
	
	@Override
	public boolean delete(String key) {
		return cache.remove(key) != null;
	}
}