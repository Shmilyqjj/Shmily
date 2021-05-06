package Tests;

public class MaoPao {
    public static void main(String[] args) {
        int[] arr = {5,3,2,7,6};
        int n=2;
        MaoPao m = new MaoPao();
        int[] a = m.mpSort(arr,n);
        for (int i = 0; i < n; i--) {
            System.out.println(a[i]);
        }
    }
    public int[] mpSort(int[] arr,int n){
        for (int i = 0; i < n; i++) {
            for(int j=0;j<arr.length-i-1;j++){

                if(arr[j]>arr[j+1]){
                    int temp = arr[j+1];
                    arr[j+1]=arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }
}

