package map;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HashMap复习(包括 LinkedHashMap)  HashMap必备基础。
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
 * 如果要存100个元素到hashMap，则初始化的时候创建 100/0.75=134的大小，防止触发扩容机制
 *
 * HashMap影响性能的因素：HashMap 的实例有两个参数影响其性能：初始容量 和加载因子。
 * 容量是哈希表中桶的数量，初始容量只是哈希表在创建时的容量。加载因子是哈希表在其容量自动增加之前可以达到多满的一种尺度。当哈希表中的条目数超出了加载因子与当前容量的乘积时，则要对该哈希表进行rehash 操作（即重建内部数据结构），从而哈希表将具有大约两倍的桶数。
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
 * HashMap长度为什么要2的n次方？如何计算index？
 * https://blog.csdn.net/zs319428/article/details/81982770
 * 求得hashcode容易发生碰撞，h & (table.length -1)来计算该对象应该保存在table数组的哪个索引处
 * 所以 index = hash & (tab.length – 1)
 * 按位与 效率较高，如果不考虑效率可以直接取余
 *
 * 哈希桶：HashMap的数组中的元素称为哈希桶
 *
 * 因为HashMap需要通过equals和hashcode两个因素来决定HashMap键的唯一性，不重写equals和hashcode得不到唯一的键
 *
 * 负载极限：  java8以后不再对全部元素重新计算hash值(rehashing)
 * hash表里还有一个“负载极限”，“负载极限”是一个0～1的数值，“负载极限”决定了hash表的最大填满程度。当hash表中的负载因子达到指定的“负载极限”时，hash表会自动成倍地增加容量（桶的数量），并将原有的对象重新分配，放入新的桶内，这称为rehashing。
 * HashMap和Hashtable的构造器允许指定一个负载极限，HashMap和Hashtable默认的“负载极限”为0.75，这表明当该hash表的3/4已经被填满时，hash表会发生rehashing。
 * “负载极限”的默认值（0.75）是时间和空间成本上的一种折中：
 * 较高的“负载极限”可以降低hash表所占用的内存空间，但会增加查询数据的时间开销，而查询是最频繁的操作（HashMap的get()与put()方法都要用到查询）
 * 较低的“负载极限”会提高查询数据的性能，但会增加hash表所占用的内存开销
 *
 * HashMap数据存取过程：
 * 存：当我们将键值对传递给put()方法时，它调用键对象的hashCode()方法来计算hashcode，然后找到bucket位置来存储值对象 如果发生哈希碰撞，用链表解决，如果链表长度超过8转为红黑树
 * 取：通过键对象的equals()方法找到正确的键值对，然后返回值对象。 HashMap在每个链表节点中储存键值对对象。当两个不同的键对象的hashcode相同时，它们会储存在同一个bucket位置的链表中，可通过键对象的equals()方法来找到键值对。
 *
 *
 * 在HashMap中，null可以作为键，这样的键只有一个，但可以有一个或多个键所对应的值为null。当get()方法返回null值时，即可以表示HashMap中没有该key，也可以表示该key所对应的value为null。因此，在HashMap中不能由get()方法来判断HashMap中是否存在某个key，应该用containsKey()方法来判断。
 *
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

        /**
         * (1)HashMap(): 构建一个空的哈希映像
         * (2)HashMap(Map m): 构建一个哈希映像，并且添加映像m的所有映射
         * (3)HashMap(int initialCapacity): 构建一个拥有特定容量的空的哈希映像
         * (4)HashMap(int initialCapacity, float loadFactor): 构建一个拥有特定容量和加载因子的空的哈希映像
         */


        /**
         * LinkedHashMap  extends HashMap<K,V>  implements Map<K,V>
         * LinkedHashMap是HashMap的子类
         * LinkedHashMap是数组加红黑树
         * 保存了记录的插入顺序，在用Iterator遍历LinkedHashMap时，先得到的记录肯定是先插入的.
         * LinkedHashMap遍历速度和容量无关，而HashMap的遍历速度和容量有关
         */

        HashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(1, "qjj");
        linkedHashMap.put(2, "abc");
        linkedHashMap.put(3, "def");
        System.out.println(linkedHashMap.toString());
    }
}
