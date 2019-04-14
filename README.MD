# Messtin Lock Redis

### Description
基于Redis的分布式锁

### Usage
```java
public class App {

    public static void main(String[] args){
        try {
            Locker.lock("1");
            System.out.println("GET ........");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Locker.release("1");
        }
    }
}
```

### Architecture
1. 如何锁住一个key？当两个客户端同时尝试获取一个锁，如何保证只有一个客户端可以获得锁?

当一个客户端尝试锁住某个key时，它会在redis上给这个key设置一个值，这个值是当前线程的host + process id + thread id。

当多个客户端都尝试获取锁时，我们使用setnx来获取锁，从而保证当某个客户端已经给key设置了值，其他客户端就无法在获取到这个锁了。

2. 如果一个线程获取锁失败了怎么办？我们如何维护它？

当一个线程获取锁失败，我们会使用CountDownLatch来阻塞线程，并将这些信息放在一个Container里面。当其他线程释放了这个锁之后，我们会重新尝试获得这个锁。

3. 如何知道其他线程是否释放了锁？

我们通过订阅channel的方式来得知其他线程释放了锁。当某个线程释放锁时，会向channel中发布一个消息，消息中包含了释放的锁的信息，我们会让正在等待这个锁的线程尝试获得这个锁。

4. 如何程序在运行的过程中，由于异常VM关闭，但是没有释放锁怎么办？

有一个shutdown hook，会在VM关闭的时候，检查是否还有锁没有释放，如果有，就会释放掉。

5. 如何设置连接Redis的host和port？

可以在classpath中添加一个文件名时redis-lock.property的文件，在里面设置host和port
```text
REDIS_SERVER_HOST=192.168.1.6
REDIS_SERVER_PORT=6379
```