package org.messin.lock.redis.pool;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class LockStatus {

    private String key;
    private String value;
    private CountDownLatch latch;

    public LockStatus(String key, String value) {
        this.key = key;
        this.value = value;
        this.latch = new CountDownLatch(1);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LockStatus that = (LockStatus) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, latch);
    }

    @Override
    public String toString() {
        return "LockStatus{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", latch=" + latch +
                '}';
    }
}
