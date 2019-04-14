package org.messin.lock.redis.pool;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The container which hold the status of locks.
 * <p>
 * {@link #waitingContainer} contains the information of thread which is wait for the lock.
 * {@link #lockingContainer} contains the information about which thread has get the lock.
 */
public final class LockStatusPool {

    /**
     * key -> queue of {@link LockStatus}
     * We use {@link Queue} here because we could pop the first thread which is waiting for the key.
     */
    private final static Map<String, Queue<LockStatus>> waitingContainer = new ConcurrentHashMap<>();

    /**
     * key -> thread information.
     */
    private final static Map<String, String> lockingContainer = new ConcurrentHashMap<>();

    public static LockStatus putIntoWaitingContainer(String key, String value) {
        LockStatus lockStatus = new LockStatus(key, value);
        return putIntoWaitingContainer(lockStatus);
    }

    public static LockStatus putIntoWaitingContainer(LockStatus lockStatus) {
        String key = lockStatus.getKey();
        if (!waitingContainer.containsKey(key)) {
            Queue<LockStatus> valueList = new ConcurrentLinkedQueue<>();
            waitingContainer.putIfAbsent(key, valueList);
        }
        waitingContainer.get(key).add(lockStatus);

        return lockStatus;
    }

    public static synchronized LockStatus peakFromWaitingContainer(String key) {
        if (waitingContainer.containsKey(key)) {
            return waitingContainer.get(key).peek();
        }
        return null;
    }

    public static void removeFromWaitingContainer(LockStatus lockStatus) {
        waitingContainer.get(lockStatus.getKey()).remove(lockStatus);
    }

    public static void putIntoLockingContainer(String key, String value) {
        lockingContainer.put(key, value);
    }

    public static void removeFromLockingContainer(String key) {
        lockingContainer.remove(key);
    }

    public static Set<String> getAllLockingLock() {
        return lockingContainer.keySet();
    }

    public static void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(new ReleaseLockWhenShutDown()));
    }
}
