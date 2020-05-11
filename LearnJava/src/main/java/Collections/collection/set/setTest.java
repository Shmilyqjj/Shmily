package Collections.collection.set;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * set 一个不包含重复元素的 collection。更确切地讲，set 不包含满足 e1.equals(e2) 的元素对 e1 和 e2，并且最多包含一个 null 元素。
 *
 *
 */
public class  setTest {
    public static void main(String[] args) {
        hashSet();
        System.out.println("-------------------");
        treeSet();
    }

    public static void hashSet(){
        /**
         * hashSet
         */
        Set<String> set = new HashSet<String>();
        set.add("tom");
        set.add("Tom");
        set.add("jack");
        set.add("rose");
        set.add("remove");
        set.add("lily");
        set.add("lily");//重复只保存一个
        set.add(null);//set最多包含一个null
        System.out.println(set.size());
        set.remove("remove");
        System.out.println(set.size());
        String[] str = set.toArray(new String[]{});
        for(String s:str){
            System.out.println(s);
        }
    }

    public static void treeSet(){
        /**
         * treeSet
         * TreeSet是一个有序的集合，它的作用是提供有序的Set集合
         * 它继承了AbstractSet抽象类，实现了NavigableSet<E>，Cloneable，Serializable接口
         * TreeSet是基于TreeMap实现的，TreeSet的元素支持2种排序方式：自然排序或者根据提供的Comparator进行排序
         */
        Set s = new TreeSet<String>();
        s.add("qjj");
        System.out.println(s.contains("qjj"));
        System.out.println(s.size());
    }




}
