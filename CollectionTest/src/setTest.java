import java.util.HashSet;
import java.util.Set;

/**
 * set 一个不包含重复元素的 collection。更确切地讲，set 不包含满足 e1.equals(e2) 的元素对 e1 和 e2，并且最多包含一个 null 元素。
 *
 *
 *
 */
public class  setTest {
    public static void main(String[] args) {
        hashSet();
    }


    /**
     * hashSet
     */
    public static void hashSet(){
        Set<String> set = new HashSet<>();
        set.add("tom");
        set.add("Tom");
        set.add("jack");
        set.add("rose");
        set.add("remove");
        set.add("lily");
        set.add("lily");//重复只保存一个
        System.out.println(set.size());
        set.remove("remove");
        System.out.println(set.size());
        String[] str = set.toArray(new String[]{});
        for(String s:str){
            System.out.println(s);
        }


    }




}
