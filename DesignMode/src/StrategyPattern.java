//策略模式（Strategy Pattern），定义了一系列的算法，将每一种算法封装起来并可以相互替换使用，策略模式让算法独立于使用它的客户应用而独立变化。
public class StrategyPattern {
    public static void main(String[] args){
     Isave is = new FileSave();
     Isave is2 = new NetSave();
     BaseService bs = new UserService();
     bs.setIsave(is);
     bs.save("filesave");
     BaseService bs2 = new StudentService();
     bs2.setIsave(is2);
     bs2.save("netsave");
    }
}

abstract class BaseService{
    public Isave isave;
    public void setIsave(Isave isave){
        this.isave = isave;
    }
    public void save(String data){
        isave.save(data);
    }

}

interface Isave{
    public abstract void save(String data);
}

class FileSave implements Isave{
    public void save(String data){
        System.out.println("保存到文件中"+data);
    }
}
class NetSave implements Isave{
    public void save(String data){
        System.out.println("保存到网络中"+data);
    }
}

class StudentService extends BaseService{

}
class UserService extends BaseService{

}