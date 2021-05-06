package Collections.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Iterator更安全，当前遍历的集合被修改时会报异常
 *Iterator单向遍历，ListIterator可以双向遍历 ListIterator继承了Iterator，有更多功能
 *
 * 首先要明白Iterator和for遍历的优缺点
 * Iterator迭代器遍历方式，适用于连续内存存储方式(数组、 ArrayList（其实 ArrayList底层实现也是数组形式）),缺点是只能从头开始遍历，优点是可以边遍历边删除。
 * for遍历灵活，可以在指定位置开始遍历，但不能边遍历边删除，否则抛异常。
 */
public class IteratorTest {
    public static void main(String[] args) {
        List<String> ls = new ArrayList<>();
        ls.add("q");
        ls.add("j");
        ls.add("j");

        //用Iterator来遍历集合
        Iterator<String> it = ls.iterator();
        while(it.hasNext()){
            String s = it.next();
            System.out.print(s+" ");
        }

        System.out.println();

        ListIterator<String> lit = ls.listIterator();
        while(lit.hasNext()){
            System.out.print(lit.next());  //hasPrevious向后遍历 ListIterator独有
        }
        lit.add("love");
        lit.add("abcd");

        System.out.println();

        for(int i = 0; i < ls.size(); i++) {
            System.out.print(ls.get(i));
        }


    }
}
