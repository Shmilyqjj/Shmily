public class DoWhile {
    public static void main(String[] args){
        int i =10;
        do {
        System.out.print(i+" ");
        i--;
        }while(i>=0);
        System.out.println();

        int j = 1;
        int temp = 1;
        int out = 0;
        do{
            temp *= j;
            out += temp;
            j++;
        }while(j<=10);
        System.out.println(out);
    }
}
