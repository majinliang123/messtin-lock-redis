package org.messin.lock.redis.pool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messin.lock.redis.connection.JedisPool;
import org.messin.lock.redis.subpub.PubSubHandler;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * When shutdown the VM,
 * use {@link ReleaseLockWhenShutDown} to delete all keys which is hold by current VM
 * and then publish message to tell other the keys is released.
 */
public class ReleaseLockWhenShutDown implements Runnable {
    private static final Logger logger = LogManager.getLogger(ReleaseLockWhenShutDown.class);

    @Override
    public void run() {
        Set<String> lockingKeys = LockStatusPool.getAllLockingLock();

        logger.info("Delete keys={} from redis and publish information.", lockingKeys);

        for (String key : lockingKeys) {
            Jedis jedis = JedisPool.newInstance(false);
            jedis.del(key);

            PubSubHandler.tellRedisLockRelease(key);
        }
    }
}
