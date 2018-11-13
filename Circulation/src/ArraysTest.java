/**
 * 功能：数组学习
 * Date/Time:2018.11.12
 */
import java.util.Arrays;
import java.util.Random;
public class ArraysTest {
    public static void main(String[] args) {

        int[] no = new int[5];//定义一个长度为5的数组 名字为no   no存在栈内，new int[]存在堆内
        no[0] = 1;
        no[1] = 2;//初始化数组，未初始化的默认为0
        System.out.println(no);//直接输出是个地址
        System.out.println(Arrays.toString(no));//想输出元素应该用Arrays.toString方法

        for(int i=0;i<no.length;i++){   //length方法是计算数组元素个数   数组的遍历
            System.out.print(no[i]+" ");
        }

        int[] no1 = new int[]{1,2,3,4,5};//定义时直接初始化

        int[] no2;
        no2 = new int[5];

        int[] no3 = {1,2,3,4,5};//定义数组并直接赋初始值---最简单常用

        //遍历方法2---foreach
        for(int x:no){
            System.out.print(x);
        }

        System.out.println();
        System.out.println("-------------------------------------");


        print(1,2,3,4,5,6);
        System.out.println("-------------------------------------");
        print1(1,2,3,4,5,6);

//        //使用数组应注意空指针异常
//        int len = no.length;
//        for(int i=0;i<=len;i++){
//            System.out.print(no[i]);
//        }


        int[] arr = new int[5];
        Random r = new Random();//生成随机数
        for(int i=0;i<arr.length;i++){
            arr[i]=r.nextInt(20);//1-20之间随机数
        }
        System.out.println(Arrays.toString(arr));

        int[][] score ={{45,78,98}, {59,80,76},{54,75,87}};//二维数组
        for(int i=0;i<3;i++){
            int num = 0;
            for(int j=0;j<3;j++){
                num += score[i][j];
            }
            System.out.println("第"+(i+1)+"个班的平均分："+num/3);

        }



    }
    //函数的可变参数
    public static void print(int ...a){  //a是可变参数 进入函数之后变成数组了
        for(int x:a){
            System.out.println(x);
        }
    }
    public static void print1(int b,int ...a){  //a是可变参数，b是普通参数，可变参数要放在普通参数之后
        System.out.println(b);
        for(int x:a){
            System.out.println(x);
        }
    }

}


