public class Dog {
    private String name;
    private int age;
    private String color;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    //构造方法


    public Dog() {  //先创建一个无参数构造方法，可以避免在反射过程中出现 java.lang.InstantiationException异常-该异常意思是不能实例化对象
        //Java反射机制会使用不带参数的构造函数来建立对象，而自己的持久化类中含有带参数的构造方法，将默认无参构造方法覆盖，导致在实例化过程出现异常。所以在定义一个无参构造方法可解决。
    }

    public Dog(String name, int age, String color) {
        this.name = name;
        this.age = age;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", color='" + color + '\'' +
                '}';
    }
}
