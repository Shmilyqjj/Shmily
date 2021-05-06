package Threads;

/**
 * 多线程---生产者和消费者案例
 * wait notify使用
 * wait 线程等待
 * wait() 与 notify/notifyAll 方法必须在同步代码块中使用
 * 调用wait()方法进入等待队列的
 * 当线程执行wait()时，会把当前的锁释放，然后让出CPU，进入等待状态。（可以调用wait方法暂停自己的执行，并且放弃已经获得的锁，然后进入等待状态）
 * 当执行notify/notifyAll方法时，会唤醒一个处于等待该 对象锁 的线程，然后继续往下执行，直到执行完退出对象锁锁住的区域（synchronized修饰的代码块）后再释放锁
 *
 * wait也会释放cpu时间片，但是会丢失显示器所有权（与sleep异同）
 *
 * date:2019.3.25
 */
public class ProducerConsumerThreadTest {
    public static void main(String[] args) {
        Food f = new Food();
        Producter p = new Producter(f);
        Customers c = new Customers(f);
        Thread t1 = new Thread(p);
        Thread t2 = new Thread(c);
        t1.start();
        t2.start();
    }
}

class Customers implements Runnable{
    private Food food;
    public Customers(Food food){
        this.food = food;
    }
    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            food.get();
        }
    }
}

class Producter implements Runnable{
    private Food food;
    public Producter(Food food){
        this.food = food;
    }
    @Override
    public void run() {
        for(int i=0;i<20;i++){
            if(i%2 == 0){
                food.set("麻辣烫","麻辣");
            }else{
                food.set("米线","酸甜");
            }
        }
    }
}

class Food{
    private String name;
    private String desc; //介绍
    private boolean flag = true; //true代表可以生产  false代表可以消费
    public Food(){

    }
    public Food(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
    //生产产品
    public synchronized void set(String name,String desc){
        //可以消费,
        if(!flag){
            //不能生产-库存满
            try {
                this.wait();//等待调用 notify()或者notifyAll()唤醒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.setName(name);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.setDesc(desc);
        flag = false;//变成可以消费
        this.notify();
    }
    //消费产品
    public synchronized void get(){
        //可以生产，不能消费-没库存
        if(flag){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.name+"->"+this.getDesc());
        this.flag = true;//变成可以生产
        this.notify();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

