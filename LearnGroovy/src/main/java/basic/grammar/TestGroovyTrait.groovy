package basic.grammar

// Groovy 特征
/**
 * 特征是语言的结构构造，允许 -
 行为的组成。
 接口的运行时实现。
 与静态类型检查/编译的兼容性
 它们可以被看作是承载默认实现和状态的接口

 特征可以用于以受控的方式实现多重继承，避免钻石问题
 */
class TestGroovyTrait {
    static void main(String[] args){
        Stu st = new Stu()
        st.StuId = 1
        st.Marks = 100

        st.DisplayMarks()
        st.DisplayTotal()
    }
}

interface Total{  //Traits 可以实现接口，在这种情况下，使用 interface 关键字声明接口
    void DisplayTotal()
}

trait Marks implements Total{
    //特征 Marks 实现了 Total 接口，因此需要为 DisplayTotal 方法提供一个实现
    int mark   // 特征可以定义属性
    void DisplayMarks(){
        println("Display Marks")
    }

    void DisplayTotal(){
        println("Display Total")
    }
}

class Stu implements Marks{
    int StuId;
    int Marks;
}
