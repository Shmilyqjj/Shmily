package Collections.map;
/**
 * TreeMap学习
 *
 * 不同点：
 * HashMap通过hashcode对其内容进行快速查找，而 TreeMap中所有的元素都保持着某种固定的顺序
 * 如果你需要得到一个有序的结果你就应该使用TreeMap（HashMap中元素的排列顺序是不固定的）
 * HashMap为了优化空间的使用，可以调优初始容量和负载因子 而TreeMap基于红黑树实现该树总平衡，没调优选项
 * 场景不同：
 *      HashMap：适用于在Map中插入、删除和定位元素。
 *      Treemap：适用于按自然顺序或自定义顺序遍历键(key)。
 *
 *
 * 相同点：
 * HashMap 非线程安全 TreeMap 非线程安全
 *
 *
 * HashMap通常比TreeMap快一点(树和哈希表的数据结构使然)，建议多使用HashMap，在需要排序的Map时候才用TreeMap。
 *
 *
 *
 * TreeMap：基于红黑树实现。TreeMap没有调优选项，因为该树总处于平衡状态。
 *
 *
 *
 *
 * (1)TreeMap():构建一个空的映像树
 * (2)TreeMap(Map m): 构建一个映像树，并且添加映像m中所有元素
 * (3)TreeMap(Comparator c): 构建一个映像树，并且使用特定的比较器对关键字进行排序
 * (4)TreeMap(SortedMap s): 构建一个映像树，添加映像树s中所有映射，并且使用与有序映像s相同的比较器排序
 */

import java.util.Map;
import java.util.TreeMap;

public class TreeMapTest {
    public static void main(String[] args) {
//        TreeMap treeMap = new TreeMap<Integer, String>();
        Map treeMap1 = new TreeMap<Integer, String>();  // TreeMap extends AbstractMap<K,V> 而 AbstractMap implements Map<K,V>
        treeMap1.put(1, "qjj");
        treeMap1.put(2, "zxw");
        treeMap1.put(3, "gjj");
        System.out.println(treeMap1.toString());
    }
}
