package basic.grammar
/**
 * Groovy 提供的设施就像java一样具有本地和全局参数。在下面的示例中，lx 是一个局部参数，它只具有 getX() 函数内的作用域，x 是一个全局属性，可以在整个 Example 类中访问。
 * 如果我们尝试访问 getX() 函数之外的变量 lx，我们将得到一个错误。
 */
class TestGroovyStaticMethod {
    static int x = 100

    static int getX(){
        int lx = 200
        println lx
        return x
    }

    static String getXStr(){
        // 可以使用this.x访问类变量
        return this.x.toString()
    }

    static void main(String[] args) {
        println(getX())
        println(getXStr())
    }
}
