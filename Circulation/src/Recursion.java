public class Recursion {
    public static void main(String[] args){
        Hanoi h = new Hanoi();
        h.method(3,"A","B","C");
    }
}

class Hanoi{
/**
 * 功能：汉诺塔
 * 1.请编写 move(n, a, b, c) 函数，它接收参数 n ，表示 3 个柱子 A、B、C
 *  中第 1 个柱子 A 的盘子数量，然后打印出把所有盘子从 A 借助 B 移动
 *  到 C 的方法
 *  2.有三根相邻的柱子，标号为A,B,C，A柱子上从下到上按金字塔状叠放着n个不同大小的圆盘，
 *  要把所有盘子一个一个移动到柱子B上，并且每次移动同一根柱子上都不能出现大盘子在小盘子上方，
 *  请问至少需要多少次移动，设移动次数为H(n）
 */
   public void method(int n,String a,String b,String c){
          if(n==1){
              System.out.println(a+"-->"+c);
          }else{
              method(n-1,a,c,b);
              method(1,a,b,c);
              method(n-1,b,a,c);
          }
    }
}