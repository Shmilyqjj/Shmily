import java.util.Arrays;

//StringBuffer效率高于String  StringBuilder效率高于StringBuffer但是有线程危险性
public class StringBufferAndStringBuilder {
    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();//类似装箱
        sb.append("qjj").append("zxw").append(666); //添加字符串
        System.out.println(sb.toString());  //输出字符串-类似拆箱
        sb.reverse();//字符串翻转
        System.out.println(sb);
        sb.delete(0,6); //删除指定位置内容
        System.out.println(sb);
        sb.replace(0,2,"*");
        System.out.println(sb);


        //带参数StringBuffer
        StringBuffer sb1 = new StringBuffer(30); //确定这个字符串最多30个字符 -- 提高效率，减少开辟空间
        StringBuffer sb2 = new StringBuffer("xyz"); //指定字符变为StringBuffer内容
        System.out.println(sb2);

        //StringBuilder用法相同
        StringBuilder sbu = new StringBuilder();//这个兼容StringBuffer的所有特性，较快
        sbu.append(666).append("qjj");
        System.out.println(sbu.toString());

    }
}

