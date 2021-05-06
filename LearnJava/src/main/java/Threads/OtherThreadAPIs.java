package Threads;

public class OtherThreadAPIs {
    public static void main(String[] args) {
        MyThreadTest mtd = new MyThreadTest();
        mtd.start();
        System.out.println();
        //void setPriority(int newPriority)   更改线程的优先级。(优先级高，可以提高该线程抢到时间片的概率大)
        mtd.setPriority(Thread.MAX_PRIORITY);
    }
}

class MyThreadTest extends Thread{
    @Override
    public void run() {
        for (int i = 0; i < 30; i++) {
            System.out.println(Thread.currentThread().getName()+"->"+i);

        }
        long i = getId();   //返回该线程的标识符。
        boolean j = isAlive();  // 测试线程是否处于活动状态。
        System.out.println("线程标识符为："+i+" 是否处于活动状态"+j);


    }
}