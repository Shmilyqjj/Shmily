package String;

public class NiuKeStringTest {
    public static void main(String[] args) {
        String s = "hello";String t = "hello";char c [ ] = {'h','e','l','l','o'};
        System.out.println(s.equals(c)); //cha[] 压根不能与String相比较，类型不是相同的
        System.out.println(s == t);  //会在字符串常量池中寻找，当找到需要的hello时，不进行字符串的创建，引用已有的
        System.out.println(t.equals (new String ("hello")));//同上

        System.out.println(getValue(2));

        System.out.println("floor:"+Math.floor(-8.5)+" "+"ceil:"+Math.ceil(-8.5)+"round:"+Math.round(-8.4));//floor: 求小于参数的最大整数。返回double类型  ceil:   求大于参数的最小整数。返回double类型  round: 对小数进行四舍五入后的结果。返回int类型

        System.out.println("-----------------------------------------------------------------");
    }
    public static int getValue(int i){
        int result = 0;
        switch (i) {
            case 1:
                result = result + i;
            case 2:
                result = result + i * 2;
            case 3:
                result = result + i * 3;
        }
        return result;
    }
}
