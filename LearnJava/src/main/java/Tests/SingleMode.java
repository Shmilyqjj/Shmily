package Tests;

public class SingleMode {
    public static void main(String[] args) {
        LazySingleton ls = LazySingleton.getInstance(); //调用类的方法获得实例化的对象
    }
}

class LazySingleton{
    private static LazySingleton m_instance = null; //1.先定义静态对象
    private LazySingleton(){//2.构造函数
        System.out.println("LS");
    }

    public static LazySingleton getInstance(){//3.输出本类的静态对象
        if(m_instance == null){
            m_instance = new LazySingleton();
        }
        return m_instance;//4.返回值 返回对象
    }
}