package map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {
    public static void main(String[] args) {
        // concurrencyLevel预计并发更新线程数（并发度） 为了兼容以前的版本  如果initialCapacity < concurrencyLevel  初始容量等于并发度
        // 与HashMap一样 链表超过一定大小会转树：
        // static final int TREEIFY_THRESHOLD = 8; 链表转树   static final int UNTREEIFY_THRESHOLD = 6; 树转链表

        // MIN_TREEIFY_CAPACITY 最小树形化容量阈值：即 当哈希表中的容量 > 该值时，才允许将链表 转换成红黑树
        // 否则，若桶内元素太多时，则直接扩容，而不是树形化 为了避免进行扩容、转换为树 两种选择的冲突，这个值不能小于 4 * TREEIFY_THRESHOLD

        //通过把整个Map分为N个Segment，可以提供相同的线程安全，但是效率提升N倍，默认提升16倍。(读操作不加锁，由于HashEntry的value变量是 volatile的，也能保证读取到最新的值。)

        //有些方法需要跨段，比如size()和containsValue()，它们可能需要锁定整个表而而不仅仅是某个段，这需要按顺序锁定所有段，操作完毕后，又按顺序释放所有段的锁

        // 扩容：段内扩容（段内元素超过该段对应Entry数组长度的75%触发扩容，不会对整个Map进行扩容），插入前检测需不需要扩容，有效避免无效扩容

        // ConcurrentHashMap替代了Hashtable

        //CoucurrentHashMap的锁分段技术：首先将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问。
        //Hashtable中采用的锁机制是一次锁住整个hash表，从而在同一时刻只能由一个线程对其进行操作，而ConcurrentHashMap中则是一次锁住一个桶。

        //ConcurrentHashMap默认将hash表分为16个桶，诸如get、put、remove等常用操作只锁住当前需要用到的桶。这样，原来只能一个线程进入，现在却能同时有16个写线程执行，并发性能的提升是显而易见的。

        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(16, 0.75f, 16);
        concurrentHashMap.put(1, "qjj");
        concurrentHashMap.put(2, "www");
        concurrentHashMap.put(3, "zzz");
        concurrentHashMap.put(4, "qg");

        System.out.println(concurrentHashMap.get(1));
    }
}
