public class DebugTest {
    public static void main(String[] args) {
        for(int i=0;i<15;i++){
            add(i);
        }
    }
    static int sum = 0;
    public static void add(int a){
        sum+=a;
        System.out.println(sum);
    }
}
