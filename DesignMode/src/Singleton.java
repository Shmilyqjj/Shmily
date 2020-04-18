public class Singleton {
    /**
     * 双重校验 保证线程安全，提高执行效率，节约内存空间
     */
    private static volatile Singleton instance;  //防止JVM指令重排
    private Singleton(){}
    public static Singleton getInstance(){
        /**
         * 如果instance不加volatile，JVM指令重排会先分配地址再初始化（此时这个地址存在但没值），
         * 所以这里判断不为null为true时有可能对象还未完成初始化，单例可能返回了一个未初始化完的对象。
         */
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
