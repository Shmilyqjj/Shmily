public class MyExceptionTest {
    public static void main(String[] args) {
        UserService u  =new User();
        UserBean ub = new UserBean();
        ub.setName("admin");
        ub.setPwd("1235");
        try{
            u.Login(ub);
        }catch(MyException e){

            System.out.println("用户名或密码错误");
        }
    }
}

class MyException extends RuntimeException{ //继承
    public MyException(){
        super();
    }
    public MyException(String str){
        super();
    }
}

abstract class UserService{
    public abstract void Login(UserBean ub);
}

class UserBean{ //用户信息   -----  封装用户信息
    private String name;
    private String pwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}

class User extends  UserService{
    public void Login(UserBean ub){
        if(!"admin".equals(ub.getName())){
            throw new MyException("用户名错误");
        }
        if(!"123".equals(ub.getPwd())){
            throw new MyException("密码错误");
        }
        System.out.println("登录成功");
        System.out.println(ub);  //输出用户信息
    }
}