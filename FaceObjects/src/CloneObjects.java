/**
 * 对象的clone 类需要实现Cloneable接口，类中需要重写Clone方法，里面调用父类Object类里的Clone方法
 * Date:2019.3.5
 */
public class CloneObjects {
    public static void main(String[] args) {
        Students s1 = new Students();
//        Students s2 = new Students();
        s1.setName("zs");
        try{
            Students s3 = (Students)s1.clone();
            s3.setName("s3");
            System.out.println(s1);
            System.out.println(s3);
        }catch (CloneNotSupportedException e){
            System.out.println("Exception");
        }


    }
}

class Students implements Cloneable{
    private String name;

    //重写Cloneable接口，返回父类的Cloneable
    protected Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    @Override
    public String toString() {
        return "Students{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}