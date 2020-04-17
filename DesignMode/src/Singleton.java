public class Singleton {
    /**
     * 双重校验 保证线程安全，提高执行效率，节约内存空间
     */
    private static Singleton instance;
    private Singleton(){}
    public static Singleton getInstance(){
        if(instance == null){
            synchronized (Singleton.class){
                if(instance == null){
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
