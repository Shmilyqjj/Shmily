package Collections.collection.list;

import java.util.*;

/**
 * 集合  ArrayList  Vector  LinkedList
 * 2018.12.8
 * 对一组相同类型的数据进行统一管理操作  集合中分为三大接口： Collection、Map、Iterator  在java.util
 *
 */
public class ListTest {
    public static void main(String[] args) {
        arrayList();
        vector();
        linkedList();
        listAdd();
        removeIfFunc();
    }

    /**
     * ArrayList  动态数组
     * 1  采用动态对象数组（Object[initialCapacity]）实现，默认构造方法创建一个空对象数组
     * 2  默认容量10   第一次添加元素，开始扩充数组容量
     * 3  扩充算法：原来数组大小+原来数组大小的一半  对应源码：int newCapacity = oldCapacity + (oldCapacity >> 1)
     * 4  不适合进行删除和插入
     * 5  为了防止数组动态扩充次数太多，建议创建ArrayList时，给定初始容量
     * 6  线程不安全，适合在单线程访问时使用
     */
    public static void arrayList(){
        // 为了防止数组动态扩充次数太多，建议创建ArrayList时，给定初始容量
        //ArrayList是继承List的类
        List<String> list = new ArrayList<>(30);
        list.add("aa");
        list.add("bb");
        list.add("cc");
        list.add("dd");
        list.add("ee");
        for(int i=0;i<list.size();i++){
            System.out.print(list.get(i)+" ");
        }
        System.out.println();

        // ArrayList 初始化
        ArrayList<String> list1 = new ArrayList<String>() {{
            add("aaa");
            add("bbb");
            add("ccc");
        }};
        list1.forEach(System.out::println);
    }



    /**
     * Vector
     *1  采用动态对象数组实现，默认构造方法创建一个长度为10的对象数组
     *2  扩充算法：当增量为0时，扩充为原来的2倍，增量大于0时，原来的大小+增量
     *3  不适合进行删除和插入
     *4  为了防止数组动态扩充次数太多，建议创建Vector时，给定初始容量
     *5  线程安全，适合在多线程访问时使用，效率较低
     */
    public static void vector(){
        //为了防止数组动态扩充次数太多，建议创建Vector时，给定初始容量
        Vector<String> v = new Vector(10);
        v.add("aa");
        v.add("bb");
        v.add("cc");
        v.add("dd");
        v.add("ee");
        for(int i=0;i<v.size();i++){
            System.out.print(v.get(i)+" ");
        }
        System.out.println();
    }

/**
 * linkedList
 * 1采用双向链表结构实现
 * 2适合插入，删除操作，性能高
 */
    public static void linkedList(){
        LinkedList<String> l = new LinkedList<>();
        l.add("aa");
        l.add("bb");
        l.add("cc");
        l.add("dd");
        l.add("ee");
        l.remove(0);
        for(String c : l){
            System.out.print(c+" ");
        }
        System.out.println();
        System.out.println(l.toString());
    }

    public static void listAdd(){
        List<String> l1 = Arrays.asList("a","b","c","d","e","f","g");
        List<String> l2 = Arrays.asList("h","i","j","k","l","m","n");
        List<String> all = new ArrayList<>();
        all.addAll(l1);
        all.addAll(l2);
        all.forEach(System.out::printf);
        System.out.println("");
    }

    public static void removeIfFunc() {
        List<String> checkedTasks = new ArrayList<>();
        checkedTasks.add("a1");
        checkedTasks.add("b1");
        checkedTasks.add("c2");
        checkedTasks.add("d2");
        checkedTasks.add("e1");
        checkedTasks.removeIf(t -> {
            System.out.println("t = " + t);
            return t.contains("2");
        });
        System.out.println(checkedTasks);
    }
}

