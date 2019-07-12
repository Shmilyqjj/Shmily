import java.util.ArrayList;
import java.util.Collections;

/**
 * 实现数组反转的不同方法
 * date:2019.4.15
 */
public class ReverseTest {
    public static void main(String[] args) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("A");
        arrayList.add("B");
        arrayList.add("C");
        arrayList.add("D");
        arrayList.add("E");
        arrayList.add("F");
        System.out.println("反转前："+arrayList.toString());

        String[] Array = { "a", "b", "c", "d", "e","f" };
        // 使用集合ArrayList实现反转
        ArrayList<String> arrayList1 = new ArrayList<>();
        for (int i = 0; i < Array.length; i++) {
            arrayList1.add(Array[Array.length - i - 1]);
        }
        String[] Array1 = arrayList1.toArray(Array);
        for (String c:Array1) {
            System.out.print(c);
        }
        System.out.println();

        //直接使用数组反转
        String[] strings = new String[Array.length];
        for (int i = 0; i < Array.length; i++) {
            strings[i] = Array[Array.length - i- 1];
        }
        for (String s:strings) {
            System.out.print(s);
        }
        System.out.println();
        System.out.println("-------------------------------------");

        //使用Collections.reverse(ArrayList)进行反转
        Collections.reverse(arrayList);
        System.out.println("Collections.reverse(ArrayList)反转后："+arrayList.toString());
    }


}
