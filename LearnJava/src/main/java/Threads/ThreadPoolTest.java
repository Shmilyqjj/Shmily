package Threads;

import java.util.concurrent.*;

/**
 * 线程池
 * 顶级接口 Executor，是一个执行线程的工具，执行已提交的Runnable任务的对象
 * ExecutorService 线程池接口
 * Executors类：这个包定义Executor ExecutorService等方法
 * Alibaba Java规范手册：创建线程不要用Executors创建，而是通过ThreadPoolExecutor，因为Executors返回的线程池有弊端，可能导致OOM
 *
 *  Executors.newSingleThreadExecutor()和Executors.newFixedThreadPool()和Executors.newCachedThreadPool()和Executors.newSingleThreadPool()底层都是ThreadPoolExecutor、
 *
 * 线程池的几个重要参数：
 * corePoolSize表示线程池的常驻核心线程数，如果设置为0，则池中无线程时自动销毁线程，如果大于0，即使没任务也会保持线程数量等于此值而不销毁线程池。 这个参数设置过小会频繁创建销毁线程，过大会浪费系统资源。
 * maximumPoolSize表示线程池可创建的最大线程数，这个值必须大于0，也必须大于corePoolSize
 * keepAliveTime线程存活时间，当线程池空闲并且空闲时间超过这个值，多余线程会销毁直到线程数量达到corePoolSize，如果corePoolSize等于MaximumPoolSize，空闲时间到了也不会销毁任何线程。
 * unit配合keepAliveTime使用，存活时间的单位
 * workQueue 线程池执行的任务队列，线程池中线程都在处理任务时，又来新任务时，缓存到这个任务队列中排队等待
 * threadFactory 线程的创建工厂 一般用默认即可（也可自己定义线程工厂，就可以自定义线程名称和优先级了）
 * RejectedExecutionHandler 线程池的拒绝策略 当workQueue缓存队列已经满了，不能用创建新线程来执行此任务时，用到该拒绝策略 限流保护机制
 *
 * Java自带四中拒绝策略
 * AbortPolicy终止策略（默认） 线程池抛异常并终止执行
 * CallerRunsPolicy 把任务交给当前线程执行
 * DiscardPolicy忽略这个任务(最新任务)
 * DiscardOldestPolicy 忽略最早的任务（最先加入队列的任务）
 *
 * 如何自定义拒绝策略：新建RejectedExecutionHandler对象然后重写rejectedExecution方法 就可以
 *
 * ThreadPoolExecutor可以扩展，通过重写beforeExecutor和afterExecutor实现。可以做一些统计线程执行前后变化或者执行计算线程池执行时间之类的操作
 */
public class ThreadPoolTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolImpl tpi = new ThreadPoolImpl();
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(tpi); //单线程的线程池  每次运行一个线程，运行完再运行下一个线程，保证线程按任务提交顺序执行  如果这个唯一的线程因异常结束，会有新的线程代替它
        System.out.println("-----------------------------");
        /**
         * 这里ExecutorService对象es可以用execute来执行线程池也可以用submit 区别：submit可以接受线程池执行的返回值而execute不能接收返回值
         */
        CallableImpl ci = new CallableImpl();
        Future<String> future = es.submit(ci);
        System.out.println(future.get());  // success
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

class CallableImpl implements Callable{
    /**
     * 1.Callable能接受一个泛型，然后在call方法中返回一个这个类型的值。而Runnable的run方法没有返回值
     * 2.Callable的call方法可以抛出异常，而Runnable的run方法不会抛出异常。
     * @return
     * @throws Exception
     */
    @Override
        public String call() throws Exception {
        for(int i=0;i<10;i++){
            System.out.println(i);
        }
        return "Success";
    }
}