package Threads;

/**
 * Interrupt  中断线程
 */
public class InterruptThread {
    public static void main(String[] args) {
       Thread t = new MyInterruptThread();
       t.start();

        MyInterruptRunnable r = new MyInterruptRunnable();
        r.setFlag(false); //自定义中断标记
        Thread tt = new Thread(r);
        tt.start();

    }
}

class MyInterruptThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            Thread.currentThread().interrupt();//只是打上中断标记，状态被置为中断状态，但不会停止线程
//            try {
//                Thread.sleep(500); //打上中断标记后睡眠会报错
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            if (Thread.currentThread().isInterrupted()){   //判断是否有中断标记，进程是否被置为中断状态
                final boolean b = interrupted(); //清除中断状态  interrupted是作用于当前线程，isInterrupted是作用于调用该方法的线程对象所对应的线程。 线程对象对应的线程不一定是当前运行的线程。例如我们可以在A线程中去调用B线程对象的isInterrupted方法。
                //isInterrupted(True/False)这个方法表示是否清除状态位）（恢复原来的状态） 如果false就直接返回状态位
                System.out.println(b);
                break;
            }
            System.out.println(Thread.currentThread().getName()+"->"+i);
        }
        System.out.println(this.isInterrupted()); //true表示该进程被标记为中断状态   flase中断状态被清除  interrupted把中断状态清除了


    }
}

//自定义线程中断
class MyInterruptRunnable implements Runnable{
    private boolean flag = true; //true表示线程没中断  flase表示线程中断状态
    public MyInterruptRunnable(){  //构造方法
        flag = true;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public void run() {
        int i = 0;
        while(flag){
            System.out.println(Thread.currentThread().getName()+"->"+i++);
        }


    }
}
