package DesignPatterns;

/**
 * 设计模式之--模版方法模式
 */
public class TempleteMethod {
    public static void main(String[] args) {
        BaseManager bm = new TeacherManager();
        bm.action("admin","add");
        bm.action("qjj","find");
    }
}

abstract class  BaseManager{
    public void action(String name,String method){
        if(name.equals("admin")){
            execute(method);
        }else{
            System.out.println("没有权限操作");
        }
    }
    public abstract  void execute(String method);
}

class UserManager extends  BaseManager{
    public void execute(String method){
        if("add".equals(method)){
           System.out.println("被添加操作...");
        }else if("find".equals(method)){
            System.out.println("做查询操作...");
        }
    }
}

class TeacherManager extends BaseManager{
    public void execute(String method){

    }
}