package map;

import java.util.HashMap;

/**
 * HashMap复习  HashMap必备基础。
 *
 * JDK1.7中HashMap组成是数组加链表    JDK1.8中的HashMap，当链表长度大于8 结构转为红黑树（因为一旦链表过长，会严重影响HashMap性能，而红黑树有快速增删改查的特点）
 *
 * JDK1.8的HashMap扩容时做了哪些优化？
 * 不像1.7那样在扩容时从新计算hash，而是采用高位运算来判断元素是否需要移动。
 *
 * 为什么加载因子是0.75？
 * -用来判断何时扩容。 （默认初始化大小DEFAULT_INITIAL_CAPACITY是16，加载因子默认是DEFAULT_LOAD_FACTOR = 0.75f，如果HashMap长度为16*0.75=12的时候，就扩容，扩容后容量是两倍）threshold=DEFAULT_INITIAL_CAPACITY*loadFactor
 * (0.5 + 1.0)/2 = 0.75
 * 综合考虑容量和性能才设置的0.75。如果加载因子较大，虽然扩容频率降低（则占用空间较小）但发生Hash冲突的几率会提高，需要更复杂的数据结构来存储元素，增加了对元素的操作时间，效率降低。
 * 如果加载因子较小，扩容频率高（占用更大空间，此时元素存储比较稀疏，发生Hash冲突可能性较小） 所以性能较高。
 *
 * HashMap元素小于等于6时还原成链表
 *
 * 当有哈希冲突，HashMap怎么查找并确认元素？
 * 查找元素是先通过数组查找，哈希冲突的元素 用链表存  如果链表长度大于8 转为红黑树存储
 *
 * HashMap源码中有哪些重要方法？
 * 查找get 添加put 扩容resize
 *
 * JDK1.7 resize并发情况下会导致死循环
 *
 * HashMap长度为什么要2的n次方？
 * https://blog.csdn.net/zs319428/article/details/81982770
 * 求得hashcode容易发生碰撞，h & (table.length -1)来计算该对象应该保存在table数组的哪个索引处
 * 按位与 效率较高，如果不考虑效率可以直接取余
 *
 * 哈希桶：HashMap的数组中的元素称为哈希桶
 */

public class HashMapTest {
    public static void main(String[] args) {
        HashMap hashMap = new HashMap();

        /**
         * hashmap的put实现方法
         * 对key的hashCode()做hash，然后再计算index;
         * 如果没碰撞直接放到bucket里；
         * 如果碰撞了，以链表的形式存在buckets后；
         * 如果碰撞导致链表过长(大于等于TREEIFY_THRESHOLD)，就把链表转换成红黑树；
         * 如果节点已经存在就替换old value(保证key的唯一性)
         * 如果bucket满了(超过load factor*current capacity)，就要resize,扩大两倍。
         */
        hashMap.put("1","qjj");
        hashMap.put("2","gjj");
        hashMap.put("3","zxw");
        hashMap.put(null,"zxw");
        hashMap.put("4",null);
        hashMap.put("5",null);

        System.out.println(hashMap.containsKey(null));
        System.out.println(hashMap.toString());
        String key = "1";
        System.out.println(key.hashCode());

        /**
         * HashMap get()
         * bucket里的第一个节点，直接命中；
         * 如果有冲突，则通过key.equals(k)去查找对应的entry
         * 若为树，则在树中通过key.equals(k)查找，O(logn)；
         * 若为链表，则在链表中通过key.equals(k)查找，O(n)。
         */
        System.out.println(hashMap.get(key));
    }
}
