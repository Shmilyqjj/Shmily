import java.util.*;
public class HelloWorld {
//    public static void main(String[] args){
//        System.out.print("Shmily");
//        Scanner in = new Scanner(System.in);
//        String s = in.nextLine();//可以读入带空格的字符串
//        System.out.println(s);
//        String s0 = in.next();//遇到空格停止
//        System.out.println(s0);
//        }
//public static void main(String[] args) {
//    Scanner in = new Scanner(System.in);
//    int d = in.nextInt();
//    switch(d){
//
//        case 7:
//            System.out.println("星期");
//            case 1:
//            System.out.println("星期一");
//            case 2:
//            System.out.println("星期2");
//            case 3:
//            System.out.println("星期3");
//            case 4:
//            System.out.println("星期4");
//            case 5:
//            System.out.println("星期5");
//            case 6:
//            System.out.println("星期6");
//
//    }
//    }
public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    double d1 = in.nextDouble();
    double d2 = in.nextDouble();
    String s = in.next();
    switch(s){
        case "+":
            System.out.println(d1+d2);
            break;
        case "-":
            System.out.println(d1-d2);
            break;
        case "*":
            System.out.println(d1*d2);
            break;
        case "/":
            System.out.println(d1/d2);
            break;
        default:
            System.out.println("运算符不正确");
    }
 }
}

