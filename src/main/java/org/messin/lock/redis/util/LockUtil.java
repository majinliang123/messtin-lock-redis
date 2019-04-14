package org.messin.lock.redis.util;

import org.messin.lock.redis.exception.MesstinLockRedisException;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class LockUtil {

    /**
     * Generate the lock information.
     * The information is combined by host, process id and thread id.
     */
    public static String getLockValueInfo() {
        long threadId = Thread.currentThread().getId();
        int processId = getProcessId();
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String host = inetAddress.getHostName();

            return host + "-" + processId + "-" + threadId;
        } catch (UnknownHostException e) {
            throw new MesstinLockRedisException(e);
        }
    }

    private static int getProcessId() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0])
                .intValue();
    }
}
