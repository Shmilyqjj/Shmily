//public class ProxyMode {
//    public static void main(String[] args) {
//        PhoneSaler ps = new ProxySaler();
//        ps.salePhone();
//    }
//}
//
//abstract class PhoneSaler{
//    public abstract void salePhone();
//}
//
//class ProxySaler extends PhoneSaler{
//    private Saler saler = null;
//
//    public void salePhone(){
//        System.out.println("代理对象 卖电话");
//    }
//    zengSong();
//    if(saler == null){
//        saler = new Saler();
//    }
//    saler.salePhone();
//    public void zengSong(){
//        System.out.println("送话费");
//    }
//}
//
//
//class Saler extends PhoneSaler
//{
//    public void salePhone()
//    {
//        System.out.println("真实角色 卖电话");
//    }
//}
