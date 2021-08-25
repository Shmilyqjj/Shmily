package basic.grammar


//特征可以用于以受控的方式实现多重继承，避免钻石问题
class TestGroovyTrait1 {
    static void main(String[] args) {
        def c = new CC()
        c.showA()
        c.showB()
    }
}

trait AA{
    void showA(){
        println("A")
    }
}

trait BB{
    void showB(){
        println("B")
    }
}

// trait可以扩展另一个trait 必须使用extends关键字
trait DD extends BB{
    void showB(){
        println("DDBB")
    }
}

class CC implements AA,BB,DD{
    @Override
    void showA() {
        println("CCAA")
    }

}