import java.util.Scanner;

public class Solution00 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long a = sc.nextLong();
        long b = sc.nextLong();
        System.out.println(GYS(a,b));
    }
    public static Long GYS(Long x,Long y){
        if(x>y){
            Long temp = x;
            x = y;
            y = temp;
        }
        while(x!=0){
            Long temp = y%x;
            y = x;
             x = temp;
        }
        return y;
    }

}

