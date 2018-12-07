import java.util.Arrays;
import java.util.Comparator;

/**
 * 功能：对象比较器
 * Comparable接口是要求自定义类去实现，按照OO原则：对修改关闭，对扩展开放。
 * Comparator接口  对一些对象的集合施加了一个整体排序
 *
 */
public class ObjectsComparation {
    public static void main(String[] args) {
        int[] a1 = {1,2,42,23,2,1,12};
        Arrays.sort(a1);
        System.out.println(Arrays.toString(a1));

        String[] s1 = {"jjfd","aewd","到了","哈哈","啊呜"};
        Arrays.sort(s1);
        System.out.println(Arrays.toString(s1));

        Dog[] ds = {new Dog("ss",2),new Dog("小白",1),new Dog("heihei",8)};
        Arrays.sort(ds);  //类的对象不可以排序 - 但是加了对象比较器就可以了
        System.out.println(Arrays.toString(ds)); //没加对象比较器会报错-RuntimeException


        Cat[] c = {new Cat("ss",2),new Cat("小白",1),new Cat("heihei",8)};
        Arrays.sort(c,new CatComparator()); // sort可以有两个参数 - 待比较的对象 和 比较器对象
        System.out.println(Arrays.toString(c));

    }
}

class Dog implements Comparable<Dog> {  //对象比较器  Comparable接口  只能和Dog类比
    private String name;
    private int age;
    public Dog(){

    }
    public Dog(String name,int age){
        this.name =name;
        this.age =age;
    }


    public int compareTo(Dog d){ //Comparable接口里的方法
//        if(this.age > d.age){ //比较两条狗的age
//             return 1;
//        }
//        if(this.age < d.age){
//            return -1;
//        }
//        return 0;
        return this.age - d.age; //代替上面的注释部分
    }


    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
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
}

class CatComparator implements Comparator<Cat>{  //Comparator接口
//    public int compare(Cat c1,Cat c2){}
//    public boolean equals(Object obj){}

      public int compare(Cat c1,Cat c2){
      return c1.getAge() -c2.getAge();
      }
}


class Cat implements Comparable<Cat> {  //对象比较器   单独定义一个Cat类比较器
    private String name;
    private int age;
    public Cat(){

    }
    public Cat(String name,int age){
        this.name =name;
        this.age =age;
    }


    public int compareTo(Cat c){ //Comparable接口里的方法
//        if(this.age > d.age){ //比较两条狗的age
//             return 1;
//        }
//        if(this.age < d.age){
//            return -1;
//        }
//        return 0;
        return this.age - c.age; //代替上面的注释部分
    }


    @Override
    public String toString() {
        return "Cat{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
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
}
