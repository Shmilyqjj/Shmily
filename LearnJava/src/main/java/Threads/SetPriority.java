package Threads;

/**
 * 线程优先级
 *
 * 看源码：
 * 线程可以拥有的最小优先级  public final static int MIN_PRIORITY=1;
 * 线程默认优先级  public final static int NORM_PRIORITY = 5;
 * 线程可以拥有的最大优先级  public final static int MAX_PRIORITY = 10
 *
 * 线程的优先级可以理解为线程抢占CPU的概率，线程优先级越高，抢占CPU的概率越高
 * 但不一定保证线程优先级高的线程一定先执行
 */
public class SetPriority {
    public static void main(String[] args) {
        MyThreadHighPriority h = new MyThreadHighPriority();
        h.setPriority(10);
        MyThreadLowPriority l = new MyThreadLowPriority();
        l.setPriority(1);
        h.start();
        l.start();
        // 结果可看到High执行频率高，High先完成 Low后完成
    }
}

class MyThreadHighPriority extends Thread {
    @Override
    public void run() {
        for (int i = 0; i <= 10; i++) {
            System.out.println("MyThreadHighPriority-" + String.valueOf(i));
        }
    }
}

class MyThreadLowPriority extends Thread {
        @Override
        public void run() {
            for (int i = 0; i <= 10; i++) {
                System.out.println("MyThreadLowPriority-" + String.valueOf(i));
            }
        }
    }
