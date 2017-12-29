package com.github.cxt.mybeimi.util.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.github.cxt.mybeimi.web.model.PlayUserClient;


@Service("multi_cache")
public class MultiCache {
	
	public Map<String, List<PlayUserClient>> cache = new HashMap<>();
	
	
	private List<PlayUserClient> getUsers(String key){
		List<PlayUserClient> users = cache.get(key);
		if(users == null){
			users = new ArrayList<>();
			cache.put(key, users);
		}
		return users;
	}
	
	public boolean delete(String key) {
		return cache.remove(key) != null;
	}
	
	public boolean delete(String key, PlayUserClient user) {
		return getUsers(key).remove(user);
	}
	
	
	public void put(String key, PlayUserClient user) {
		getUsers(key).add(user);
	}
	
	
	public List<PlayUserClient> getCacheObject(String key) {
		List<PlayUserClient> dataList = getUsers(key) ; 
		List<PlayUserClient> values = new ArrayList<PlayUserClient>();
		for(Object data : dataList){
			values.add((PlayUserClient) data) ;
		}
		
		Collections.sort(values, new Comparator<PlayUserClient>(){

			@Override
			public int compare(PlayUserClient o1, PlayUserClient o2) {
				return o1.getPlayerindex() > o2.getPlayerindex() ? 1 : -1;
			}
			
		});
		return values;
	}

	
}