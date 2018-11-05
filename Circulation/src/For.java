public class For {
    public static void main(String[] args) {
//         for(int i = 0 ; i <=5 ; ){
//             ++i;
//             System.out.println(i);
//         }
//         for(int i = 0; ;i++){
//              System.out.println("死循环1");
//         }

//         for(;;){
//             System.out.println("死循环2");
//         }

//        for(int i=0;i<=200;i++){
//             if(i%7 == 0 && i%4!=0){
//                 System.out.println(i);
//             }
//        }

        //Fib
         int num1 = 1;
         int num2 = 1;
         int numn;
         int h;
         for(h=1;h<=10;h++){
         if(h==1){
             System.out.println(num1);
         }else if(h==2){
             System.out.println(num2);
         }else{
             numn = num1+num2;
             num1 = num2;
             num2 = numn;
             System.out.println(numn);
         }
        }


    }
}

