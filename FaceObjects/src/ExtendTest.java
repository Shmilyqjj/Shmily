import java.util.Arrays;

public class ExtendTest{
    public static void main(String[] args) {
//        SortCosmeticManager sc = new SortCosmeticManager();
//        sc.Add(new Cosmetic("雅诗兰黛","进口",1000));
//        sc.Add(new Cosmetic("香奈儿","进口",800));
//        sc.Add(new Cosmetic("大宝","国产",20));
//        sc.Add(new Cosmetic("郁美净","国产",5));
//        sc.show();

        JinkouCosmeticManager jk = new JinkouCosmeticManager();
        jk.Add(new Cosmetic("雅诗兰黛","进口",1000));
        jk.Add(new Cosmetic("香奈儿","进口",800));
        jk.Add(new Cosmetic("大宝","国产",20));
        jk.Add(new Cosmetic("郁美净","国产",5));
        jk.show();
    }
}

class JinkouCosmeticManager extends CosmeticManager{
    public void show(){
        Cosmetic[] jkcm = Arrays.copyOf(cm,cm.length);//jkcm是cm的一个备份
//        for(int i=0;i<count;i++){
//            if(jkcm[i].getType().equals("进口")){
//                jkcm[i].print();
//            }
//
//        }
        //用foreach
        for (Cosmetic c:cm) {
            if(null != c){
                if(c.getType().equals("进口")){
                    c.print();
                }
            }

        }
    }
}

class SortCosmeticManager extends CosmeticManager{
    public void show(){
//        Cosmetic[] cs = new Cosmetic[5];
        Cosmetic[] cs = Arrays.copyOf(cm,cm.length);//cs 是 cm 的一个备份
        for(int i =0;i<count;i++){
            for(int j=i;j<count;j++){
                if(cs[i].getPrice() > cs[j].getPrice()){
                    Cosmetic temp = cs[i];
                    cs[i] = cs[j];
                    cs[j] = temp;
                }
            }
        }
        for (Cosmetic c:cs) {
            if(null!=c){
                c.print();
            }
        }
    }
}

class CosmeticManager{
    Cosmetic[] cm = new Cosmetic[5];
    protected int count = 0;
    protected void Add(Cosmetic c){
        if(this.cm.length == this.count){
            int len = this.cm.length*2;
            cm = Arrays.copyOf(cm,len);
        }
        cm[count] = c;
        count++;
    }

    public void show(){
        for (Cosmetic c:cm) {
            if(null!=c){
                c.print();
            }
        }
    }
}

class Cosmetic{
    private String name;
    private String type;
    private int price;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }



    public Cosmetic(String name,String type,int price){
        this.name = name;
        this.type = type;
        this.price = price;
    }
    public void print(){
        System.out.println(name+","+type+","+price);
    }
}