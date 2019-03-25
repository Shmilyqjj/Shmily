/**
 * 线程的创建 创建线程的两种方法
 */
public class CreateThread{
    public static void main(String[] args) {
        Thread t = new MyThreadExt();
        t.start();//就绪状态  等待时间片去执行时才开始运行

        System.out.println("-----------------------------------------");

        //Runnable本身不能用，需要new一个线程，把Runnable的对象放入线程里
        Runnable r = new MyRunnableImpl();
        Thread tt = new Thread(r);
        tt.start();

    }
}

class MyThreadExt extends Thread{  //创建一个线程方法1：继承Thread类
    @Override
    public void run() { //需要重写run方法
        for (int i = 0; i < 1000; i++) {
            System.out.println(Thread.currentThread().getName()+"-->"+i); //Thread.currentThread().getName()输出当前线程名  继承Thread的时候可以用this代替Thread.currentThread()
        }
    }
}

class MyRunnableImpl implements Runnable{ //创建一个线程方法2：实现Runnable接口
    @Override
    public void run() { //写run方法
        for (int i = 0; i < 1000; i++) {
            System.out.println(Thread.currentThread()+"-->"+i);
        }
    }
}