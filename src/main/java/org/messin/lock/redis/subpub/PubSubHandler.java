package org.messin.lock.redis.subpub;

import org.messin.lock.redis.config.Config;
import org.messin.lock.redis.connection.JedisSingleton;
import redis.clients.jedis.Jedis;

public final class PubSubHandler {

    public static void init() {
        subscribeChannel();
    }

    public static void tellRedisLockRelease(String key) {
        Jedis jedis = JedisSingleton.newInstance();
        jedis.publish(Config.LOCK_CHANNEL, key);
    }

    private static void subscribeChannel() {
        Jedis jedis = JedisSingleton.newInstance(true);
        new Thread(()->{
            jedis.subscribe(new Subscriber(), Config.LOCK_CHANNEL);
        }).start();
    }
}
