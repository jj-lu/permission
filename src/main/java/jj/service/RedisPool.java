package jj.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

@Service("redisPool")
@Slf4j
public class RedisPool {

    @Resource
    private ShardedJedisPool shardedJedisPool;


    /**
     * 获取redis客户端对象
     * @return
     */
    public ShardedJedis instance(){
        return shardedJedisPool.getResource();
    }

    /**
     * 关闭客户端
     * @param shardedJedis
     */
    public void safeClose(ShardedJedis shardedJedis){
        try{
            if (shardedJedis != null){
                shardedJedis.close();
            }
        }catch (Exception e){
            log.error("return redis resources exception error :" + e);
        }
    }
}
