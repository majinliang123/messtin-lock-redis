package org.messin.lock.redis.subpub;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messin.lock.redis.config.Config;
import org.messin.lock.redis.connection.JedisPool;
import redis.clients.jedis.Jedis;

public final class PubSubHandler {

    private static final Logger logger = LogManager.getLogger(PubSubHandler.class);

    public static void init() {
        subscribeChannel();
    }

    public static void tellRedisLockRelease(String key) {
        logger.info("Publish information to release key={}.", key);

        Jedis jedis = JedisPool.newInstance(false);
        jedis.publish(Config.LOCK_CHANNEL, key);
    }

    private static void subscribeChannel() {
        Jedis jedis = JedisPool.newInstance(true);
        new Thread(() -> jedis.subscribe(new Subscriber(), Config.LOCK_CHANNEL))
                .start();
    }
}
