public class TryCatchTest {
    public static void main(String[] args) {
//        div(10,5);
        div(10,0);
    }
    public static int div(int a,int b){
        int arr[] = {1,2,3,4,5,6,7,8,9,10};
//        arr = null;
        try{
            System.out.println(arr.length);
            return a/b;
        }catch(NullPointerException e){
            System.out.println("空指针异常");
        }catch(ArithmeticException e){
            System.out.println("数学计算异常，除数不能为0");
        }catch(Exception e){
            System.out.println("其他异常");
        }finally{
            System.out.println("我是finally，我比return先执行");
            System.out.println("前面有return就会自动检测finally，如果有finally就先执行finally，如果finally里有return就执行finally中的return 之前的return不执行");
//            return 2;
        }
        return -1; //函数返回值 ，遇到异常返回-1
    }
}
