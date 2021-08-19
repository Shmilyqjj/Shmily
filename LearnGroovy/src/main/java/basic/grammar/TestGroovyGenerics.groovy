package basic.grammar

/**
 * Groovy 泛型
 * 在定义类，接口和方法时，泛型使能类型（类和接口）作为参数。
 * 与在方法声明中使用的更熟悉的形式参数非常类似，类型参数提供了一种方法，可以为不同的输入重复使用相同的代码。区别在于形式参数的输入是值，而类型参数的输入是类型。
 */
class TestGroovyGenerics {
    static void main(String[] args) {
        List<String> list = new ArrayList<String>()
        // 在主程序中，注意我们能够声明ListType类的对象，但是不同类型的对象。第一个类型是Integer类型，第二个类型是String类型。
        ListType<String> lt = new ListType<>()
        lt.set("First String")
        println(lt.get())

        ListType<Integer> lt1 = new ListType<>()
        lt1.set(23)
        println(lt1.get())

    }
}

//泛型类
// 整个类也可以泛化。这使得类更灵活地接受任何类型，并相应地与这些类型工作。
//我们正在创建一个名为ListType的类。注意放置在类定义前面的<T>关键字。这告诉编译器这个类可以接受任何类型。因此，当我们声明这个类的一个对象时，我们可以在声明期间指定一个类型，并且该类型将在占位符<T>。
//泛型类有简单的getter和setter方法来处理类中定义的成员变量。
public class ListType<T> {
    private T t

    public T get() {
        return this.t
    }

    public void set(T plocal) {
        this.t = plocal
    }
}