/**
 * 小问题汇总  细节问题
 */
///**
// * 最大公约数 int级别
// */
//public class Solution00 {
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        long a = sc.nextLong();
//        long b = sc.nextLong();
//        System.out.println(GYS(a,b));
//    }
//    public static Long GYS(Long x,Long y){
//        if(x>y){
//            Long temp = x;
//            x = y;
//            y = temp;
//        }
//        while(x!=0){
//            Long temp = y%x;
//            y = x;
//             x = temp;
//        }
//        return y;
//    }
//
//}


import java.util.Scanner;

// 整数取平方根 取整
public  class  Solution00{
    public static void main(String[] args) {


//        //method01
//        Scanner in = new Scanner(System.in);
//        int a = in.nextInt();
//        int result = (int) Math.floor(Math.sqrt(a));
//        System.out.println(result);

//        //method02  -立方根
//        Scanner in = new Scanner(System.in);
//        int a = in.nextInt();
//        int result = (int)Math.cbrt(a);
//        System.out.println(result);

         //method03 -四次方根  / n次方根
        Scanner in = new Scanner(System.in);
        double a = in.nextDouble();
        double result = Math.pow(a, 1d / 4);
        System.out.println(result);


    }
}