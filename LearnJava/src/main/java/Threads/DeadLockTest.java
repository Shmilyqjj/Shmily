package Threads;

/**
 * 死锁
 * 死锁：过多的同步可能出现死锁，死锁一般在程序运行时才有可能出现   多线程中要进行资源的共享就需要同步，但同步过多就可能死锁
 * date:2019.3.25
 */
public class DeadLockTest {
    public static void main(String[] args) {
    new DeadLockRunnable();
    }
}

class DeadLockRunnable implements Runnable{
    Consumer c = new Consumer();
    Waiter w = new Waiter();
    public DeadLockRunnable(){
        new Thread(this).start();  //相当于执行run ->顾客说先吃饭再买单
        w.say(c);//同时又执行Water的say方法 -> 服务员说先买单再吃饭
        //所以可能死锁
    }
    @Override
    public void run() {
        c.say(w);
    }
}

class Consumer{
    public synchronized void say(Waiter w){
        System.out.println("顾客：先吃饭后买单");
        w.doService();
    }
    public synchronized void doService(){
        System.out.println("同意了买完单再吃饭");
    }
}

class Waiter{
    public synchronized void say(Consumer c){
        System.out.println("服务员：先买单后吃饭");
        c.doService();
    }
    public synchronized void doService(){
        System.out.println("同意了吃完饭再买单");

    }
}