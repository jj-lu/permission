package jj.service;


import com.google.common.base.Joiner;
import jj.beans.CacheKeyConstants;
import jj.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

@Service
@Slf4j
public class SysCacheService {

    @Resource(name = "redisPool")
    private RedisPool redisPool;

    public void saveCache(String toSaveValue, int timeoutSeconds, CacheKeyConstants prefix){
        saveCache(toSaveValue,timeoutSeconds,prefix,null);
    }

    public void saveCache(String toSaveValue, int timeoutSeconds, CacheKeyConstants prefix,String... keys){
        if (toSaveValue == null){
            return;
        }
        ShardedJedis shardedJedis = null;
        try {
            String cacheKey = generateCacheKey(prefix, keys);
            shardedJedis = redisPool.instance();
            shardedJedis.setex(cacheKey,timeoutSeconds,toSaveValue);
            log.info("使用redis缓存存储信息：key："+cacheKey+",value:"+toSaveValue);
        }catch (Exception e){
            log.error("save cache exception , prefix:{},keys:{}",prefix.name(), JsonMapper.obj2String(keys),e);
        }finally {
            redisPool.safeClose(shardedJedis);
        }
    }

    public String getFromCache(CacheKeyConstants prefix,String... keys){
        String cacheKey = generateCacheKey(prefix, keys);
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = redisPool.instance();
            String value = shardedJedis.get(cacheKey);
            log.info("使用redis缓存获取得信息："+value);
            return value;
        }catch (Exception e){
            log.error("get from cache exception, prefix:{},keys:{}",prefix.name(),JsonMapper.obj2String(keys),e);
            return null;
        }finally {
            redisPool.safeClose(shardedJedis);
        }
    }

    private String generateCacheKey(CacheKeyConstants prefix,String... keys){
        String key = prefix.name();
        if (keys != null && keys.length > 0){
            key += "_" + Joiner.on("_").join(keys);
        }
        return key;
    }
}
