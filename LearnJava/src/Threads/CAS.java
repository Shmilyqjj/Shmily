import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Home-PC
 */
public class CAS{
    /**
     * CAS是 CPU原子指令 CompareAndSwap
     * @param args
     */
    public static void main(String[] args) {
    AtomicInteger atomicInteger = new AtomicInteger(6);
    boolean firstCAS = atomicInteger.compareAndSet(6, 2019);
    System.out.println(firstCAS + "\t" + atomicInteger.get());
    boolean secondCAS = atomicInteger.compareAndSet(6, 2000);
    System.out.println(secondCAS + "\t" + atomicInteger.get());
    }

}
