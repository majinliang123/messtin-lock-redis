package org.messin.lock.redis.config;

/**
 * Some config params.
 * <p>
 * Some properties have default value if it set here and some don't have.
 * <p>
 * We could change it by set it at file redis-lock.property at folder resources like below:
 * <p>
 * # redis-lock.property
 * REDIS_SERVER_HOST=192.168.1.6
 * REDIS_SERVER_PORT=6379
 * <p>
 * But there are some properties is {@code final }, we could not set it at redis-lock.property any more.
 * Their value is constant.
 */
public final class Config {

    public static final String LOCK_CHANNEL = "MESSTIN_LCOK_CHANNEL";

    public static String REDIS_SERVER_HOST;
    public static String REDIS_SERVER_PORT;
}
