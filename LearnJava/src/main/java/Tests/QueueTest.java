package Tests;

import java.util.LinkedList;
import java.util.Queue;
/*
* Queue： 基本上，一个队列就是一个先入先出（FIFO）的数据结构
* Queue接口与List、Set同一级别，都是继承了Collection接口。LinkedList实现了Deque接口。
*
* 　add        增加一个元索                     如果队列已满，则抛出一个IIIegaISlabEepeplian异常
　　remove   移除并返回队列头部的元素    如果队列为空，则抛出一个NoSuchElementException异常
　　element  返回队列头部的元素             如果队列为空，则抛出一个NoSuchElementException异常
　　offer       添加一个元素并返回true       如果队列已满，则返回false
　　poll         移除并返问队列头部的元素    如果队列为空，则返回null
　　peek       返回队列头部的元素             如果队列为空，则返回null
　　put         添加一个元素                      如果队列满，则阻塞
　　take        移除并返回队列头部的元素     如果队列为空，则阻塞
*
*
*
* */
public class QueueTest {
    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<String>();
        ((LinkedList<String>) queue).add("abc");
        queue.offer("String");
        queue.offer("String1");
        queue.offer("String2");
        System.out.println(queue.poll());
        System.out.println(queue.remove());
        System.out.println(queue.size());
        System.out.println(queue.element());


    }
}
