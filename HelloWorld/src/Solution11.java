/**
 * 盒马是阿里巴巴新零售战略的重要组成部分，作为线下的新物种，一家盒马门店的成败，开店时候的地址选择非常重要。结合阿里巴巴海量数据，我们把选址问题抽象如下：把地图划分为n*m的矩阵方格，如果某地区的人口和消费力满足一定的盈利要求，那么这块地区标为1，否则标为0。 任何一个2*2的子矩阵，如果4个格子都是1，就满足开一家盒马店铺的要求。同时为了资源最大化利用，不同盒马店铺之间覆盖的区域要求不能有重叠。请问在给定的n*m的矩阵内，最多可以开多少家盒马店铺？示例图如下所示：
 * 输入:
 * 第一行为一个数字n，表示矩阵的行数；
 * 第二行为一个数字m，表示矩阵的列数；
 * 接下来一行，为n*m个由0和1数字组成的字符串，以空格分隔。表示n*m矩阵，前m个数字为第一行、接下来m个数字为第二行、以此类推。
 * 输出:
 * 输出最多可以开店的个数。
 * 输入范例:
 * 3
 * 3
 * 1 1 1 1 1 1 1 0 1
 * 输出范例:
 * 1
 */

//80%
//public class Solution11 {
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int n = sc.nextInt();
//        int m = sc.nextInt();
//        int[][] arr = new int[n][m];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                arr[i][j] = sc.nextInt();
//        }
//        }
//        int count = 0;
//        for (int i = 0; i < n-1; i++) {
//            for (int j = 0; j < m-1; j++) {
//                if(arr[i][j] ==1 && arr[i+1][j] ==1 && arr[i][j+1]==1 && arr[i+1][j+1] ==1){
//                    i = i+2;
//                    j = j+2;
//                    count += 1;
//                }
//            }
//
//        }
//        System.out.println(count);
//    }
//}


//public class Solution11 {
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int n = sc.nextInt();
//        int m = sc.nextInt();
//        int[][] arr = new int[n][m];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                arr[i][j] = sc.nextInt();
//            }
//        }
//        int count = 0;
//        for (int i = 0; i < n-1; i++) {
//            for (int j = 0; j < m-1; j++) {
//                if(arr[i][j] ==1 && arr[i+1][j] ==1 && arr[i][j+1]==1 && arr[i+1][j+1] ==1){
//                    i = i+2;
//                    j = j+2;
//                    count += 1;
//                }
//                if(i==n-2 || j==n-2){
//                    break;
//                }
//            }
//
//        }
//        System.out.println(count);
//    }
//}




