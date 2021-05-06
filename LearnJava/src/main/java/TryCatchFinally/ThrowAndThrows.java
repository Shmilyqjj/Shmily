package TryCatchFinally;

/**
 * throws 在声明方法的时候使用，抛出异常给这个方法的调用者处理
 * throw  手动抛出异常给调用者，可以自定义异常类型
 */
public class ThrowAndThrows {
    public static void main(String[] args) {
        try{
            divs(10,1);
        }catch(NullPointerException e) {
            e.printStackTrace();//打印错误信息
        }

        try{
            div(10,0);
        }catch (ArithmeticException e){
            System.out.println("除数不能为0");
        }
    }
    public static void divs(int a,int b)throws ArithmeticException{ //抛出异常
        if(b == 1){
            //人为抛出异常可以写在任何位置
            throw new NullPointerException("除数为1，没有意义"); //可以没参数
        }
        int c = a/b;
        System.out.println("throw Test");
    }

    public static void div(int a,int b)throws ArithmeticException{ //抛出异常
        int c = a/b;
        System.out.println("throws Test");
    }
}
