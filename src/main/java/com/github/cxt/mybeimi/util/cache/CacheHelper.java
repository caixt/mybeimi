package com.github.cxt.mybeimi.util.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;
import org.cache2k.event.CacheEntryExpiredListener;
import org.cache2k.expiry.ExpiryPolicy;
import com.github.cxt.mybeimi.core.BMDataContext;
import com.github.cxt.mybeimi.core.engine.game.BeiMiGameTask;
import com.github.cxt.mybeimi.web.model.GameRoom;


public class CacheHelper {
	private static CacheHelper instance = new CacheHelper();
	private static Map<String, Queue<GameRoom>> queueMap = new HashMap<>();
	private final Cache<String, BeiMiGameTask> expireCache ;
	public CacheHelper(){
		expireCache = new Cache2kBuilder<String, BeiMiGameTask>() {}
			.sharpExpiry(true)
			.eternal(false)
			.expiryPolicy(new ExpiryPolicy<String, BeiMiGameTask>() {
				@Override
				public long calculateExpiryTime(String key, BeiMiGameTask value,
						long loadTime, CacheEntry<String, BeiMiGameTask> oldEntry) {
					return value.getCacheExpiryTime();
				}
			})
		    .addListener(new CacheEntryExpiredListener<String, BeiMiGameTask>() {
				@Override
				public void onEntryExpired(Cache<String, BeiMiGameTask> cache,
						CacheEntry<String, BeiMiGameTask> task) {
					task.getValue().execute();
				}
			})
	    .build();
	}
	
	/**
	 * 获取缓存实例
	 */
	public static CacheHelper getInstance(){
		return instance ;
	}
    //userId:roomId	
	public static CacheBean getRoomMappingCacheBean() {
		return BMDataContext.getContext().getBean(OnlineCache.class);
	}
	//gamePlaywayId:gamePlayway
	public static CacheBean getSystemCacheBean() {
		return BMDataContext.getContext().getBean(SystemCache.class);
	}
	//roomId:room	
	public static CacheBean getGameRoomCacheBean() {
		return BMDataContext.getContext().getBean(GameRoomCache.class);
	}
	//roomId:userList
	public static MultiCache getGamePlayerCacheBean() {
		return BMDataContext.getContext().getBean(MultiCache.class);
	}
	//userId:playUserClient,tokenId:token
	public static CacheBean getApiUserCacheBean() {
		return BMDataContext.getContext().getBean(ApiUserCache.class);
	}
	public static CacheBean getBoardCacheBean() {
		return BMDataContext.getContext().getBean(GameCache.class);
	}
	
	public static Queue<GameRoom> getQueneCache(String playway){
		Queue<GameRoom> queue = queueMap.get(playway);
		if(queue != null){
			return queue;
		}
		synchronized (CacheHelper.class) {
			queue = queueMap.get(playway);
			if(queue != null){
				return queue;
			}
			queue = new ArrayBlockingQueue<>(100);
			queueMap.put(playway, queue);
			return queue;
		}
	}
	
	public static Cache<String, BeiMiGameTask> getExpireCache(){
	    return instance.expireCache;
	}
}
