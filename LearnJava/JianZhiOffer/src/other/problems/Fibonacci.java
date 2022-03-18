package other.problems;

/**
 * :Description: 斐波那契数列
 * :Author: 佳境Shmily
 * :Create Time: 2022/3/13 17:10
 * :Site: shmily-qjj.top
 * 有一对兔子，从出生后第3个月起每个月都生一对兔子，小兔子长到第三个月后每个月又生一对兔子，假如兔子都不死，问每个月的兔子总数为多少？
 * 解：兔子对儿数 1 1 2 3 5 8 13 ...
 */
public class Fibonacci {
    public static void main(String[] args) {
        System.out.println(fib(10) * 2);
        System.out.println(fib1(10) * 2);
        System.out.println(fib(2) * 2);
        System.out.println(fib1(2) * 2);
        System.out.println(fib(3) * 2);
        System.out.println(fib1(3) * 2);
    }

    // 递归
    public static int fib(int n){
        if(n == 1 || n == 2){
            return 1;
        }else {
            return fib(n-1) + fib(n-2);
        }
    }

    // 非递归
    public static int fib1(int n){
        int f1 = 1;
        int f2 = 1;
        int f = 0;
        if(n == 1 || n == 2 ){
            return 1;
        }
        for(int i=3; i <= n; i++){
            f = f1 + f2;
            f2 = f1;
            f1 = f;
        }
        return f;
    }
}
