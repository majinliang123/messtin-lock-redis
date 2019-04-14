package org.messin.lock.redis.subpub;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messin.lock.redis.connection.JedisPool;
import org.messin.lock.redis.pool.LockStatus;
import org.messin.lock.redis.pool.LockStatusPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Subscribe on channel {@link org.messin.lock.redis.config.Config#LOCK_CHANNEL}
 * When there are message from the channel, it means some one release the lock,
 * we will try to get the lock.
 */
public class Subscriber extends JedisPubSub {
    private Logger logger = LogManager.getLogger(Subscriber.class);

    @Override
    public void onMessage(String channel, String message) {
        logger.info("Receive message={} from channel={}", message, channel);

        Jedis jedis = JedisPool.newInstance(false);

        LockStatus lockStatus = LockStatusPool.peakFromWaitingContainer(message);
        if (lockStatus != null && jedis.setnx(lockStatus.getKey(), lockStatus.getValue()) == 1) {
            LockStatusPool.removeFromWaitingContainer(lockStatus);
            LockStatusPool.putIntoLockingContainer(lockStatus.getKey(), lockStatus.getValue());
            lockStatus.getLatch().countDown();
            logger.info("Give lock {} to {}.", lockStatus.getKey(), lockStatus.getValue());
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        logger.info("Subscribe channel={} and subscribedChannels={}.", channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        logger.info("Unsubscribe channel={} and subscribedChannels={}.", channel, subscribedChannels);
    }
}
