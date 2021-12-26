/**
 * 打印图形合集
 */
import java.util.Scanner;
public class ForImage {
    public static void main(String[] args) {

//图1
//        Scanner in = new Scanner(System.in);
//        int n = in.nextInt();
//        for(int i=1;i<=n;i++){
//            for(int j=1;j<=i;j++){
//                System.out.print("*");
//            }
//            System.out.println();
//        }


//图4
//          Scanner in = new Scanner(System.in);
//          int n = in.nextInt();
//          for(int i=1;i<=n;i++){
//              for(int j=1;j<=(n-i);j++){
//                  System.out.print(" ");
//              }
//              for(int k=1;k<=2*i-1;k++){
//                  System.out.print("*");
//              }
//              System.out.println();
//          }


//图2
//           int n = 9;
//           for(int i=1;i<=n;i++){
//               for(int j=1;j<=i;j++){
//                   System.out.print(j+"X"+i+"="+i*j+" ");
//               }
//               System.out.println();
//           }


//图3
//              Scanner in = new Scanner(System.in);
//              int n = in.nextInt();
//              for(int i=1;i<=n;i++){
//                  for(int j=i;j<=n-1;j++){
//                      System.out.print(" ");
//                  }
//                  for(int k=1;k<=i;k++){
//                      System.out.print("*");
//                  }
//                  System.out.println();
//              }


//空心三角形
//                Scanner in = new Scanner(System.in);
//                int n = in.nextInt();
//                for(int i=1;i<=n;i++){
//                    for(int j=1;j<=n-i;j++){
//                        System.out.print(" ");
//                    }
//                    for(int k=1;k<=2*i-1;k++){
//                       if(i==1 || i==n){
//                           System.out.print("*");
//                       }else{
//                           if(k==1 || k==2*i-1){
//                               System.out.print("*");
//                           }else{
//                               System.out.print(" ");
//                           }
//                       }
//                    }
//                    System.out.println();
//                }


//空心菱形
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        for (int i = 1; i <= n; i++) {
            for(int j=1;j<=n-i;j++){
                System.out.print(" ");
            }
            for(int k=1;k<=2*i-1;k++)
            {
                if(i==1){
                    System.out.print("*");
                }else{
                    if(k==1||k==2*i-1){
                        System.out.print("*");
                    }else{
                        System.out.print(" ");
                    }

                }
            }
            System.out.println();
        }

        //倒立
        for(int l=n-1;l>=1;l--){
            for(int m=n-l;m>=1;m--){
                System.out.print(" ");
            }
            for(int o=2*l-1;o>=1;o--){
                if(l==1){
                    System.out.print("*");
                }
                else{
                    if(o==1||o==2*l-1){
                        System.out.print("*");
                    }else{
                        System.out.print(" ");
                    }
                }
            }
            System.out.println();
        }
    }
}




