package org.messin.lock.redis.subpub;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messin.lock.redis.pool.LockStatus;
import org.messin.lock.redis.pool.LockStatusPool;
import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {
    private Logger logger = LogManager.getLogger(Subscriber.class);

    @Override
    public void onMessage(String channel, String message) {
        logger.info("Receive message={} from channel={}", message, channel);

        LockStatus lockStatus = LockStatusPool.popFromWaitingContainer(message);
        if (lockStatus != null){
            LockStatusPool.putIntoLockingContainer(lockStatus.getKey(), lockStatus.getValue());
            lockStatus.getLatch().countDown();
            logger.info("Give lock {} to {}.", lockStatus.getKey(), lockStatus.getValue());
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {

    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {

    }
}
