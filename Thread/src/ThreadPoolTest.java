import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池
 * 顶级接口 Executor，是一个执行线程的工具，执行已提交的Runnable任务的对象
 * ExecutorService 线程池接口
 * Executors类：这个包定义Executor ExecutorService等方法
 */
public class ThreadPoolTest {
    public static void main(String[] args) {
        ThreadPoolImpl tpi = new ThreadPoolImpl();
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(tpi); //单线程的线程池  每次运行一个线程，运行完再运行下一个线程，保证线程按任务提交顺序执行  如果这个唯一的线程因异常结束，会有新的线程代替它

        System.out.println("-----------------------------");

        ExecutorService ftp = Executors.newFixedThreadPool(10); //创建固定大小的线程池，每次提交任务都创建一个线程，直到线程达到线程池最大大小，达到最大大小，线程池的线程数就保持不变，如有异常退出的线程，有新线程来补上
        ftp.execute(tpi);
        es.shutdown();//关闭线程池
        ftp.shutdown();//关闭线程池
        //newCachedThreadPool创建可缓存的线程池，如果线程池大小超过处理任务需要的线程大小，就会回收部分空闲的线程，任务数增加时，只能添加新线程来处理任务

        //newScheduledThreadPool创建一个大小无限的线程池，此线程支持定期以及周期性执行任务的需求
    }
}

class ThreadPoolImpl implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(i);
        }
    }
}