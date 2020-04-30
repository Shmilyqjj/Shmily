package Threads;

/**
 * 守护线程   线程分守护线程和用户线程
 * public final void setDaemon(boolean on) 将此线程标记为daemon线程或用户线程。 当运行的唯一线程都是守护进程线程时，Java虚拟机将退出。
 * public final boolean isDaemon()  测试这个线程是否是守护线程。
 * 用户线程执行完时，只剩下一个守护线程，则JVM退出
 */
public class DaemonThread {
    public static void main(String[] args) {
        DaemonRunnable dr = new DaemonRunnable();
        Thread t = new Thread(dr);
        t.setDaemon(true);  //定义该线程是守护线程还是用户线程  true代表守护线程
        t.start();

        for (int i = 0; i < 20; i++) {
            System.out.println(Thread.currentThread().getName()+"->"+i);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class DaemonRunnable implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            System.out.println(Thread.currentThread().getName()+"->"+i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}