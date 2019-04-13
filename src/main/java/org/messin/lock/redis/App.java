package org.messin.lock.redis;

public class App {

    public static void main(String[] args){
        try {
            Locker.lock("1");
            System.out.println("GET ........");
//            Thread.sleep(100000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
        } finally {
//            Locker.release("1");
            System.exit(0);
        }

    }
}
