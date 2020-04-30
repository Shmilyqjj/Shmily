package Tests;

import java.text.DecimalFormat;
import java.util.Scanner;

public class JiSuanTest {
    public static void main(String[] args) {
        System.out.println("请输入各项数据：\n法人机构全称：");
        Scanner in = new Scanner(System.in);
        String frjgqc = in.next();
        System.out.println("法人机构年限：");
        int jgjynx = in.nextInt();
        if(jgjynx<4){
            System.err.println("不符合准入标准！");
            System.exit(0);
        }
        System.out.println("养殖成本：");
        double yzcb = in.nextDouble();
        if(yzcb>13){
            System.err.println("不符合准入标准！");
            System.exit(0);
        }
        System.out.println("现有存栏量：");
        int xycll = in.nextInt();
        System.out.println("最大养殖规模：");
        int zdyzgm = in.nextInt();
        if((((double)(zdyzgm-xycll)/zdyzgm))<0.2 ){
            System.err.println("不符合准入标准！");
            System.exit(0);
        }
        System.out.println("机构所在区域：");
        String jgszqy = in.next();
        System.out.println("最近一个区域生猪价格：");
        double qyszjg = in.nextDouble();
        System.out.println("猪价数据平均值：");
        double zjsjpjz = in.nextDouble();
        System.out.println("补栏规模：");
        int blgm = in.nextInt();
        System.out.println("输入5年的主营业务收入：");
        double[] zyywsr = new double[5];
        Double Tzyywsr = 0.00;
        for (int i = 0; i < 5; i++) {
            zyywsr[i]=in.nextDouble();
            Tzyywsr += zyywsr[i];
        }
        System.out.println("输入5年的营业利润：");
        double[] yylr = new double[5];
        Double Tyylr = 0.00;
        for (int i = 0; i < 5; i++) {
            yylr[i] = in.nextDouble();
            Tyylr += yylr[i];
        }
        if((Tyylr/Tzyywsr)<0.12){
            System.err.println("不符合准入标准！");
            System.exit(0);
        }
        System.out.println(frjgqc+"符合规则-计算结果为：");
        System.out.println("补栏规模:"+(zdyzgm-xycll));
        System.out.println("平均利润率："+(Integer.parseInt(new DecimalFormat("0").format((Tyylr/Tzyywsr)*100))+"%"));
        System.out.println("利润空间："+(qyszjg-yzcb));
        System.out.println("最大可能营收："+((zjsjpjz*zdyzgm*150000)/10000));
        System.out.println("可能主营成本："+((yzcb*zdyzgm*150000)/10000));
        System.out.println("补栏资金投入："+((blgm*yzcb*150000)/10000));

        System.out.println("计算完成，输入Y并回车退出程序");
        if(in.next().equals("Y")){
            System.exit(0);
        }
    }
}

