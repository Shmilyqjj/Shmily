/**
 * join Thread中的join方法
 * 等待线程死亡 - 线程暂停 类似wait 不是死亡
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