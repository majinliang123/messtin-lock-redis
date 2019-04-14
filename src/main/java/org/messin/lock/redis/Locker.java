package org.messin.lock.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messin.lock.redis.connection.JedisPool;
import org.messin.lock.redis.pool.LockStatus;
import org.messin.lock.redis.pool.LockStatusPool;
import org.messin.lock.redis.subpub.PubSubHandler;
import org.messin.lock.redis.util.LockUtil;
import redis.clients.jedis.Jedis;

/**
 * The centre class of this project.
 * Use {@link Locker} to handle their lock.
 */
public final class Locker {
    private static Logger logger = LogManager.getLogger(Locker.class);

    static {
        logger.info("Init Locker.");
        JedisPool.init();
        PubSubHandler.init();
        LockStatusPool.init();
    }

    public static void lock(String key) throws InterruptedException {
        logger.info("Try to get lock for key={}.", key);

        Jedis jedis = JedisPool.newInstance(false);
        String lockValue = LockUtil.getLockValueInfo();
        if (jedis.setnx(key, lockValue) == 0) {
            logger.info("Not get the lock for {}, put it into waiting container.", key);
            LockStatus lockStatus = LockStatusPool.putIntoWaitingContainer(key, lockValue);
            try {
                lockStatus.getLatch().await();
            } catch (InterruptedException e) {
                LockStatusPool.removeFromWaitingContainer(lockStatus);
                throw e;
            }
        } else {
            logger.info("Got the lock for {}, put it into locking container.", key);
            LockStatusPool.putIntoLockingContainer(key, lockValue);
        }
    }

    public static void release(String key) {
        logger.info("Release the key={}.", key);
        LockStatusPool.removeFromLockingContainer(key);

        Jedis jedis = JedisPool.newInstance(false);
        jedis.del(key);

        PubSubHandler.tellRedisLockRelease(key);
    }

}
