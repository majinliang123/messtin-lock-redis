package org.messin.lock.redis.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messin.lock.redis.config.Config;
import org.messin.lock.redis.exception.MesstinLockRedisException;
import org.messin.lock.redis.util.ConfigUtil;
import redis.clients.jedis.Jedis;

/**
 * Create jedis instance use double check lock.
 */
public final class JedisSingleton {
    private static final Logger logger = LogManager.getLogger(JedisSingleton.class);

    private static volatile Jedis INSTANCE;

    public static Jedis newInstance() {
        if (INSTANCE == null) {
            synchronized (JedisSingleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = createInstance();
                }
            }
        }
        return INSTANCE;
    }

    public static Jedis newInstance(boolean createNew) {
        if (createNew) {
            return createInstance();
        } else {
            return newInstance();
        }
    }

    private static Jedis createInstance() {
        try {
            logger.info("Creating instance for Jedis.");
            ConfigUtil.loadConfig();

            if (Config.REDIS_SERVER_HOST != null && Config.REDIS_SERVER_PORT != null) {
                return new Jedis(Config.REDIS_SERVER_HOST, Integer.parseInt(Config.REDIS_SERVER_PORT));
            } else if (Config.REDIS_SERVER_HOST != null) {
                return new Jedis(Config.REDIS_SERVER_HOST);
            } else {
                return new Jedis();
            }
        } catch (Exception e) {
            throw new MesstinLockRedisException(e);
        }
    }
}
