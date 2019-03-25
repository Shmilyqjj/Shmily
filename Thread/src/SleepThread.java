/**
 * 线程休眠-让线程以xxx毫秒暂停（暂时停止执行）
 * 释放CPU时间片
 * 线程不会丢失任何显示器的所有权
 * public static void sleep(long millis) throws InterruptedException
 * IllegalArgumentException - 如果 millis值为负数
 * InterruptedException - 如果任何线程中断当前线程。 当抛出此异常时，当前线程的中断状态将被清除。
 */
public class SleepThread {
    public static void main(String[] args) {
        MyThread m = new MyThread();
        m.start();
    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(500); //sleep 500毫秒 - 半秒
                System.out.println(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}