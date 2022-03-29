package faceObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * :Description: Java泛型 协变 逆变  不变性  泛型擦除
 * :Author: 佳境Shmily
 * :Create Time: 2022/3/29 14:06
 * :Site: shmily-qjj.top
 * 对象只作为泛型的生产者时用? extends
 * 对象只作为泛型的消费者时用? super
 */
public class Genericity {
    public static void main(String[] args) {
        List<Double> doubleList = new ArrayList<>();
        // Double是继承自Number的但函数入参是List<Number>类型，实际传List<Double>类型编译不通过
        // 所以要使用泛型的协变  sum(List<Number>改为 sum(List<? extends Number>
        // ? extends 使得泛型可以协变  这里的协变是：List<Double>可以被认为是List<Number>的子类
        // Number为Double、Integer、Short的父类 通过“? extends E”后，使得子类型的泛型对象可以赋
        // 改成Number后对子类的要求更宽松
        // 通过协变 泛型对数据类型的要求更宽松  但泛型协变后 无法再向代码里添加数据了
        // 协变 放宽对子类型的约束
        System.out.println(sum(doubleList));
        List<Integer> integerList = new ArrayList<>();
        System.out.println(sum(integerList));

        System.out.println("###########################################");

        // 逆变 放宽对父类型的约束  ? super
        Filter<Double> filter = new Filter<Double>() {
            @Override
            public boolean test(Double elem) {
                // 大于100 filter返回true
                return elem > 100;
            }
        };
        List<Double> doubleList1 = new ArrayList<>();
        doubleList1.add(1001.0);doubleList1.add(99.0);doubleList1.add(100.1);
        removeIf(doubleList1, filter).forEach(System.out::println);
        // 如果过滤int类型要Integer Short类型要Short 太麻烦 但Double、Integer、Short的父类都是Number，放宽父类的类型限制 用泛型的逆变
        // 对removeIf函数的参数Filter<E> filter做修改 改为 Filter<? super E> filter使用逆变 则支持Double、Integer、Short
        // Double原本为Number的子类 通过“? super E”后，继承关系逆变，Number类型的Filter可以认为是逆变后Double类型Filter的子类，使得父类型的泛型对象可以赋值
        List<Integer> integerList1 = new ArrayList<>();
        integerList1.add(1001);integerList1.add(99);integerList1.add(100);
        Filter<Integer> filterSuper = new Filter<Integer>() {
            @Override
            public boolean test(Integer elem) {
                // 大于100 filter返回true
                return elem > 100;
            }
        };
        removeIf(integerList1, filterSuper).forEach(System.out::println);
        // 但无法拿到这个泛型的类型了只能变成顶级的Object

        System.out.println("###########################################");

        // 泛型擦除
        // java文件 -> javac编译 -> javap解析字节码  得到的字节码看到有泛型被擦除(ArrayList<String>被解析为ArrayList)但有其他地方记录了泛型的具体信息
        // 擦了等于没擦  class文件中还会记录泛型的信息

    }


    public static double sum(List<? extends Number> list){
        // 泛型协变后 无法再向代码里添加数据了 如果可以添加数据就无法保证list中的数据都是同一类型的了
        // list.add(3);
        double result = 0;
        for (Number n:list) {
            result += n.doubleValue();
        }
        return result;
    }

    interface Filter<E>{
        boolean test(E elem);
    }
//    public static <E> List<E> removeIf(List<E> list, Filter<E> filter){
    public static <E> List<E> removeIf(List<E> list, Filter<? super E> filter){
        List<E> removedList = new ArrayList<>();
        for (E e:list) {
            if(filter.test(e)){
                // 按filter条件过滤
                removedList.add(e);
            }
        }
        list.removeAll(removedList);
        return removedList;
    }


}
