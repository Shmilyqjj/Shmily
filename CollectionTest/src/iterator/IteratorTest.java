package iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Iterator更安全，当前遍历的集合被修改时会报异常
 *
 * Iterator单向遍历，ListIterator可以双向遍历 ListIterator继承了Iterator，有更多功能
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
        lit.add("zz");

        System.out.println();

        for (int i = 0; i < ls.size(); i++) {
            System.out.print(ls.get(i));
        }


    }
}
