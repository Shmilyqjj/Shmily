package Threads;

/**
 * join Thread中的join方法
 * 等待线程死亡 - 线程暂停 类似wait 不是死亡
 *
 * 在一个线程中调用 other.join() ，这时候当前线程会让出执行权给 other 线程，直到 other 线程执行完或者过了超时时间之后再继续执行当前线程
 *
 * 本来两个线程轮流执行，一个线程调用了另一个线程的join后，让出执行权给另一个线程join(2000)则先让另一个线程执行两秒再轮流执行
 */
public class JoinThread {
    public static void main(String[] args) {
       MyJoinThread mj = new MyJoinThread();
       mj.start();
       MyJoinThread0 mj0 = new MyJoinThread0();
       mj0.start();
    }
}
class MyJoinThread extends Thread{
    @Override
    public void run() {
        for (int i = 0; i < 30; i++) {
            try {
                if(i==20){
                    this.join(6000); //无参数就代表线程中断（类似睡眠，不是死亡）,等其他线程运行完任然不继续运行  有参数代表停止时间，等待其他进程运行完后，时间过了，继续运行本线程
                }
                System.out.println(Thread.currentThread().getName()+"->"+i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class MyJoinThread0 extends Thread{
    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            System.out.println(Thread.currentThread().getName()+"->"+i);
        }
    }
}