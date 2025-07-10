package com.system.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class RedisUtil {
    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
        // 这里可根据实际配置文件读取参数
        jedisPool = new JedisPool("localhost", 6379);
    }

    @PreDestroy
    public void destroy() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public void set(String key, String value, int expireSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, expireSeconds, value);
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }
} 