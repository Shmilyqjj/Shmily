import sun.awt.windows.ThemeReader;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程同步 ！！！
 *解决数据共享问题，要使用同步
 *同步 作用：多个线程在同一时间段内只能有一个线程执行指定代码，其他线程要等待此线程执行完之后才能继续执行
 *不同步的话，对数据存取可能会出现多种情况多种结果
 * 同步代码块中，不要阻塞
 * 同步方法尽量不要调用同步方法
 * date:2019.3.23
 */
public class ThreadSynchronized {
    public static void main(String[] args) {
        MyTicketThread mtt = new MyTicketThread();
//        Thread t1 = new Thread(mtt);
//        Thread t2 = new Thread(mtt);//两个线程同时执行同一段代码
//        t1.start();
//        t2.start();

        System.out.println("-------------线程同步三种方式---------------");

        //1.同步代码块
        SynchronizedBlock sb = new SynchronizedBlock();
//        Thread t3 = new Thread(sb);
//        Thread t4 = new Thread(sb);
//       t3.start();
//        t4.start();

        //2.同步方法
        SynchronizedMethod sm = new SynchronizedMethod();
//        Thread t5 = new Thread(sm);
//        Thread t6 = new Thread(sm);
//        t5.start();
//        t6.start();

        //3.Lock(ReentrantLock)方法
        LockMethod lm = new LockMethod();
        Thread t7 = new Thread(lm);
        Thread t8 = new Thread(lm);
        t7.start();
        t8.start();
    }
}

class MyTicketThread implements Runnable{
    private int tickets = 300;
    @Override
    public void run() {
        for(int i=0;i<300;i++){
            tickets --;
            if(tickets>0 && tickets<10){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("剩余票数："+tickets);
            }
        }
    }
}

//同步代码块
class SynchronizedBlock implements Runnable{
    private int tickets = 300;
    private Object o = new Object();
    @Override
    public void run() {
        for(int i=0;i<300;i++){
            synchronized(o){   //同步代码块 参数是Object对象  这时这段代码只能一个线程执行，其他线程要等待
                //synchronized括号里参数是个对象，决定锁的作用域，作用范围  如果是object对象就是作用于全局
                tickets --;
                if(tickets>=0 && tickets<10){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("剩余票数："+tickets);
                }  //同步代码块到这执行完毕 同步锁释放掉，其他线程去争着执行代码块
            }
        }
    }
}

class SynchronizedMethod implements Runnable{
    private int tickets = 300;
    @Override
    public void run() {
        for (int i = 0; i < 300; i++) {
            method();
        }
    }
    public synchronized void method(){ //相当于synchronized(this) 锁加到当前对象
        tickets --;
        if(tickets>=0 && tickets<=10){
            try {
                Thread.sleep(1000); //释放CPU时间片但不会释放锁
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+" 剩余票数："+tickets);
        }
    }
}

class LockMethod implements Runnable{
    private int tickets = 300;
    private Lock lock = new ReentrantLock();
    @Override
    public void run() {
        for (int i = 0; i < 300; i++) {
            LockMethod();
        }
    }

    public void LockMethod(){
        lock.lock();  //获得锁  代码块执行完后还要 unlock释放锁
        tickets --;
        if(tickets>=0 && tickets<=10){
            try {
                Thread.sleep(1000); //释放CPU时间片但不会释放锁
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+" 剩余票数："+tickets);
        }
        lock.unlock(); //如果lock和unlock之间代码有异常，不会释放锁，所以一般加try catch块 finally中加lock.unlock()  lock是Lock的对象
    }



}