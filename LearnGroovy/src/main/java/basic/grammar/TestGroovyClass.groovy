package basic.grammar

//Groovy 面向对象编程
class TestGroovyClass {
    int StudentID;
    String StudentName;
    private int age;
    static void main(String[] args) {
        TestGroovyClass testGroovyClass = new TestGroovyClass()
        testGroovyClass.StudentID = 1
        testGroovyClass.StudentName = 'qjj'
        testGroovyClass.setAge(23)
        println testGroovyClass.getAge()
        // -------------------
        Outer outer = new Outer()
        outer.name = "qjj"
        outer.callInnerMethod()
        def b = new B('qjj')
        b.exec()
        def c = new C()
        c.DisplayMarks()
    }

    int getAge() {
        return age
    }

    void setAge(int age) {
        this.age = age
    }
}


class Person{
    public String name
    public Person(String n){
        name = n
    }
}
// 继承
class Student extends Person{
    int StuId

    Student(String n) {
        super(n)
    }
}



//Inner class
// 内部类在另一个类中定义。封闭类可以像往常一样使用内部类。另一方面，内部类可以访问其封闭类的成员，即使它们是私有的。不允许除封闭类之外的类访问内部类。
class Outer {
    String name;

    def callInnerMethod() {
        new Inner().methodA()
    }

    class Inner {
        def methodA() {
            println(name);
        }
    }

}

//Abstract class
//抽象类表示通用概念，因此，它们不能被实例化，被创建为子类化。他们的成员包括字段/属性和抽象或具体方法。抽象方法没有实现，必须通过具体子类来实现。
//抽象类必须用抽象关键字声明。抽象方法也必须用抽象关键字声明。

abstract class A {  // 不能实例化抽象类A
    public String name;
    public A() { }
    abstract void exec();  //抽象方法，没有具体实现，必须通过子类实现 必须用abstract修饰
    void start(){  //具体方法 有具体实现
        println(String.format("Name: %s  -- start", name))
    }
}
class B extends A{
    public B(String n){
        name = n
    }
    @Override
    void exec() {  // 继承抽象类  必须实现其抽象方法
        println(String.format("Name: %s %s -- exec", super.name, name))
    }
}

// Interface接口
// 接口定义了类需要遵守的契约。接口仅定义需要实现的方法的列表，但是不定义方法实现。需要使用interface关键字声明接口。接口仅定义方法签名。
//接口的方法总是公开的。在接口中使用受保护或私有方法是一个错误。
interface I{
    void DisplayMarks()
}

class C implements I{

    @Override
    void DisplayMarks() {
        println("Math == 150")
    }
}
