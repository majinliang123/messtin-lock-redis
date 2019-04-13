package org.messin.lock.redis.pool;

import org.messin.lock.redis.connection.JedisSingleton;
import org.messin.lock.redis.subpub.PubSubHandler;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class ReleaseLockWhenShutDown implements Runnable {
    @Override
    public void run() {
        Set<String> lockingKeys = LockStatusPool.getAllLockingLock();
        for (String key : lockingKeys) {
            Jedis jedis = JedisSingleton.newInstance();
            jedis.del(key);

            PubSubHandler.tellRedisLockRelease(key);
        }
    }
}
