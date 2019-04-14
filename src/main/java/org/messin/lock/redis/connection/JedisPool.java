package org.messin.lock.redis.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messin.lock.redis.config.Config;
import org.messin.lock.redis.exception.MesstinLockRedisException;
import org.messin.lock.redis.util.ConfigUtil;
import redis.clients.jedis.Jedis;

/**
 * Create jedis instance.
 */
public final class JedisPool {
    private static final Logger logger = LogManager.getLogger(JedisPool.class);

    private static volatile Jedis INSTANCE;

    public static void init() {
        try {
            ConfigUtil.loadConfig();
        } catch (Exception e) {
            throw new MesstinLockRedisException(e);
        }
    }

    /**
     * Create instance of {@link Jedis}
     * <p>
     * If {@code createNew } is false, will use the jedis instance already cached.
     * If {@code createNew } is true, will create a new jedis instance and return.
     */
    public static Jedis newInstance(boolean createNew) {
        if (createNew) {
            return createInstance();
        } else {
            return newInstance();
        }
    }

    private static Jedis newInstance() {
        if (INSTANCE == null) {
            synchronized (JedisPool.class) {
                if (INSTANCE == null) {
                    INSTANCE = createInstance();
                }
            }
        }
        return INSTANCE;
    }

    private static Jedis createInstance() {
        try {
            logger.info("Creating instance for Jedis.");
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
