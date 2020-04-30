package map;
/**
 * Map是个接口,有四个实现类,分别是HashMap Hashtable LinkedHashMap 和TreeMap.
 */

import java.util.*;

public class MapTest {
    public static void main(String[] args) {
        Map map = new HashMap<Integer,String>();
        map.put(1, "q");
        map.put(2, "j");
        map.put(3, "j");
        System.out.println(map.containsKey(4));
        System.out.println(map.containsValue("q"));
        System.out.println(map.get(2));

        System.out.println("**********************************");
        Map map1 = new Hashtable();  // Hashtable实现了MAP(K,V)
        map1.put(1, "q");
        map1.put(2, "j");
        map1.put(3, "j");
        System.out.println(map1.toString());
    }
}
