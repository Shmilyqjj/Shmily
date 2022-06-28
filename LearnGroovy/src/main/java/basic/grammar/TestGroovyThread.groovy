package basic.grammar

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class TestGroovyThread {
    static void main(String[] args) {


        // Future+Callable
        def futureList = futureCallableTask()
        futureList.each {
            println it.get()
        }
    }

    static def futureCallableTask(){
        def st = System.currentTimeMillis()
        List<Future<Map<String, Object>>> futureList = []
        ExecutorService es = Executors.newFixedThreadPool(2)
        for (int i = 0; i < 10; i++) {
            futureList.add(es.submit(new FutureTask(i)))
        }
        es.shutdown()
        while (!es.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println("线程池没有关闭")
        }
        System.out.println("线程池已经关闭")
        println(costs: (System.currentTimeMillis() - st) / 1000 + "s")
        return futureList
    }
}


class FutureTask implements Callable<Map<String, Object>> {
    int id
    FutureTask(int id){
        this.id = id
    }
    @Override
    Map<String, Object> call() throws Exception {
        println("execute FutureTask id:" + this.id + " thread id:" + Thread.currentThread().getId())
        Thread.sleep(3000)
        println("execute FutureTask id:" + this.id + " thread id:" + Thread.currentThread().getId())
        return [key: System.currentTimeMillis()]
    }
}

