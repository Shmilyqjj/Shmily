import java.io.IOException;

public class RuntimeClass {
    public static void main(String[] args) throws IOException {
        Runtime rt = Runtime.getRuntime();
        System.out.println("处理器数量：" + rt.availableProcessors()+" 个");
        System.out.println("Jvm总内存数 ："+ rt.totalMemory()+" byte");
        System.out.println("Jvm空闲内存数： "+ rt.freeMemory()+" byte");
        System.out.println("Jvm可用最大内存数： "+ rt.maxMemory()+" byte");

        rt.exec("notepad");//执行程序，进程名notepad

    }
}
