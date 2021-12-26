/**
 * 功能：数组学习
 * Date/Time:2018.11.12
 */
import java.util.Arrays;
import java.util.Random;
public class ArraysTest {
    public static void main(String[] args) {
//1.数组的定义和遍历
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
//2.注意事项
//        //使用数组应注意空指针异常
//        int len = no.length;
//        for(int i=0;i<=len;i++){
//            System.out.print(no[i]);
//        }

//3.二维数组 随机数
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



//4.Arrays类  常用方法
        //二分查找
        int[] arr1 = {2,5,22,34,57,89,96};
        int index0 = Arrays.binarySearch(arr1,89);//下标为5
        int index1 = Arrays.binarySearch(arr1,100);//返回负数 绝对值为8  大于最大的 从-1开始到-(length+1)
        int index2 = Arrays.binarySearch(arr1,1); //返回负数 绝对值为1
        int index3 = Arrays.binarySearch(arr1,6); //返回负数 绝对值为3  -1开始 在中间取
        System.out.println(index0+" "+index1+" "+index2+" "+index3);

        //数组按字符串输出 toString
        System.out.println(Arrays.toString(arr1));

        //数组排序
        int[] arr2 = {2,7,1,9,15,2,86,125,36};
        Arrays.sort(arr2);//这个函数没有返回值 直接改变原数组
        System.out.println(Arrays.toString(arr2));

        //复制指定的数组 Arrays.copyOf(int[] array,int length);
        int[] arr3 = Arrays.copyOf(arr2,6);
        System.out.println(Arrays.toString(arr3));
        //复制指定的数组 Arrays.copyOf(int[] array,int from,int to);
        int[] arr4 = Arrays.copyOfRange(arr2,4,6);
        System.out.println(Arrays.toString(arr4));

        //判断数组相等Arrays.equals(arr1,arr2)
        System.out.println(Arrays.equals(arr1,arr2));

        //使用指定元素填充数组
        Arrays.fill(arr1,10);//所有都填充
        System.out.println(Arrays.toString(arr1));
        Arrays.fill(arr2,2,4,10);//下标2，3填充
        System.out.println(Arrays.toString(arr2));
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


