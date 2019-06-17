package thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-10-19 09:29
 **/
public class DeadLockThread {

    private static Object        object = new Object();
    private static CyclicBarrier cb  = new CyclicBarrier(2);

    public static void main(String[] args) {
        new ReadLockThread().start();
        new WriteLockThread().start();


    }


    static class ReadLockThread extends Thread {

        @Override
        public void run() {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + " is running...");
                try {
                    Thread.sleep(10000);
                    cb.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    static class WriteLockThread extends Thread {

        @Override
        public void run() {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + " is running...");
                try {
                    cb.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
