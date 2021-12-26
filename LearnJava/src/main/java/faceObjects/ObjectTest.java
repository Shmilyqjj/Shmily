package faceObjects;

public class ObjectTest {
    public static void main(String[] args) {
        Student s = new Student(1,"Zs",14);
        System.out.println(s.toString());

        //默认执行类的toString方法
        System.out.println(s);

        Student s0 = new Student(2,"ls",18);
        Student s1 = new Student(2,"ls",18);
//        Object o = new Object();
        System.out.println(s0.equals(s1));

        System.out.println(s0.getClass());  //返回此object运行时的类名
    }
}

class Student{
    private int sid;
    private String name;
    private int age;
    public Student(int sid,String name,int age){
        this.sid = sid;
        this.name = name;
        this.age = age;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    //重写toString
//    public String toString(){
//        return sid+", name = "+name+",age = "+age;
//    }


//重写equals
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj instanceof Student){
            Student s = (Student)obj;
            if(this.sid != s.sid){
                return false;
            }

        if(!this.name.equals(s.name)){
                return false;
        }
        if(this.age != s.age){
                return false;
        }
            return true;
        }
        return false;

    }

    @Override  //右键generate 自动生成
    public String toString() {
        return "faceObjects.Student{" +
                "sid=" + sid +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}