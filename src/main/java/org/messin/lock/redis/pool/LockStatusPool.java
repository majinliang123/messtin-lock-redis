package org.messin.lock.redis.pool;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class LockStatusPool {

    private final static Map<String, Queue<LockStatus>> waitingContainer = new ConcurrentHashMap<>();
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

    public static synchronized LockStatus popFromWaitingContainer(String key) {
        if (waitingContainer.containsKey(key)) {
            return waitingContainer.get(key).poll();
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
        String removedValue = lockingContainer.remove(key);
    }

    public static Set<String> getAllLockingLock(){
        return lockingContainer.keySet();
    }

    public static void init(){
        Runtime.getRuntime().addShutdownHook(new Thread(new ReleaseLockWhenShutDown()));
    }
}
