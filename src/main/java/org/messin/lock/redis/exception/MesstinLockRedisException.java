package org.messin.lock.redis.exception;

public class MesstinLockRedisException extends RuntimeException {

    public MesstinLockRedisException() {
        super();
    }

    public MesstinLockRedisException(String message) {
        super(message);
    }

    public MesstinLockRedisException(Throwable throwable) {
        super(throwable);
    }
}
