package basic.grammar

// Groovy 闭包
// 闭包是一个短的匿名代码块。它通常跨越几行代码。一个方法甚至可以将代码块作为参数。它们是匿名的。
class TestGroovyClosure {
    static void main(String[] args) {
        // 代码行 - {println“Hello World”}被称为闭包。此标识符引用的代码块可以使用call语句执行
        def clos = {println("closure")}
        clos.run()
        clos.call()

        // 带一个参数的闭包 注意使用$ {param}，这导致closure接受一个参数。当通过clos.call语句调用闭包时，我们现在可以选择将一个参数传递给闭包
        def clos1 = {param->println "Hello ${param}"}
        clos1.call("World")

        //闭包调用变量
        def str1 = "Hello"
        def clos2 = {param -> println "${str1} ${param}"}
        clos2.call("Groovy")

        // 集合使用闭包 遍历
        def list = [1,2,3,4]
        list.each {println(it)}
        println(list.find {it > 2})  //寻找list里第一个大于2的值

        // 寻找list所有大于2的值列表
        def val = list.findAll{it > 2}
        val.each {println it}

        // any 寻找list中至少一个为真的
        println(list.any{it > 5 })

        // every 寻找list中所有为真
        println(list.every{it > 0 })

        // collect 将闭包作为转换器 转换list所有值
        println(list.collect{it * it})

        // Map使用闭包 遍历
        def map = [1:"qjj"/* 这是个map 也可以叫dict */, 2: /* 注释 */"abc"]
        map.each {println("key: ${it.key} value: ${it.value}")}

        // 闭包跳出循环
        def l = [1,2,3]
        l.each {
            if(it == 1){
                println("Matched.Continue.")
                return true  // return true在each闭包操作中起到continue的作用 跳过本轮循环后续的代码 继续下一轮循环
            }
            println("Not Match.")
        }
        l.find{
            if(it == 2){
                println("Matched.Break.")
                return true // return true在find闭包操作中起到break的作用 跳出整个循环
            }
            println("Not Match.")
        }
    }
}
