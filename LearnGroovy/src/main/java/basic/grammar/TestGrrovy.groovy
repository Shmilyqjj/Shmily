package basic.grammar
// import packages
import groovy.xml.MarkupBuilder

class TestGroovy {
    static void main(String[] args) {
        //Example of a int datatype
        int x = 5;

        //Example of a long datatype
        long y = 100L;

        //Example of a floating point datatype
        float a = 10.56f;

        //Example of a double datatype
        double b = 10.5e40;

        //Example of a BigInteger datatype
        BigInteger bi = 30g;

        //Example of a BigDecimal datatype
        BigDecimal bd = 3.5g;

        println(x);
        println(y);
        println(a);
        println(b);
        println(bi);
        println(bd);

        test_var()
        test_operator()
        test_while()
        println(sum(1,))
        num()
        str()
        range()
        list()
        map()
        date()
        regex()
        tryCache()
    }

    static void test_var(){
        // init objects
        def xml = new MarkupBuilder()

        // define variables
        def student1
        def student_name
        String x = "QJJ"
        def _Name = "qjj"
        int X = 6
        println(String.format("%s == %s %s", _Name, x, X))
    }

    static void test_operator(){
        def range = 0..5
        println(range)
        println(range.get(2))

    }

    static void test_while(){
        // while
        int count = 0
        while(count<5) {
            count++;
        }
        //for
        for(int i = 0;i < 5; i++) {
            // do operation{
            if(i == 2){
                continue
            }
            if(1){
                break
            }
        }
        //for in
        int[] array = [0,1,2,3];
        for(int i in array) {
            println(i);
        }
        //for in 遍历map
        def map = ["a":1,"b":2,"c":3]
        for(j in map) {
            println(j)
        }
    }

    static int sum(int a, int b=0){
        return a + b
    }

    static void num(){
        Integer x = 5
        Float y = 1.25
    }

    static void str(){
        String sample = "Hello World"
        println(sample[4]); // Print the 5 character in the string
        //Print the 1st character in the string starting from the back
        println(sample[-1]);
        println(sample[1..2]);//Prints a string starting from Index 1 to 2
        println(sample[4..2]);//Prints a string starting from Index 4 back to 2
        println "s".compareToIgnoreCase("S")  // 比较字符串忽略大小写
        println "s".concat("a")  //拼接
        println("qjj".replace("j", "k"))
    }

    static void range(){
        println(1..10)
        println(1..<10)
        println('a'..'x')
        println(10..1)
        println('x'..'a')
    }

    static void list(){
        def l1 = []
        def l2 = [1,2,3,4,5]
        l1.add(2)
        l2 = l2 + l1 // 合并列表
        l2 = l2.plus(l1)  // 合并列表
        println(l1)
        println(l2)
        l2 = l2.minus(l1)
        println(l2) //删除另一个列表包含的元素
        println(l1.contains(2))
        println(l2.contains(2))
        println(l2.get(2))
        println(l1.isEmpty())
        println(l2.pop())  //删除最后一个元素
        println(l2.sort())
        println(l2.size())
    }

    static void map(){
        def m1 = ['name': "qjj", "age": 23]
        def m2 = [:] //空map
        println(m1)
        println(m1.keySet())  //获取所有key
        println(m1.values())  //获取所有值
        m1.put("id", 1) //没有则添加一条kv对
        m1.put("id", 2) //key存在则替换值
        println(m1.size())
        println(m2.containsValue(23))
        println(m1.containsKey('id'))
        println(m1.get('id'))
    }

    static void date(){
        Date date = new Date()
        println(date.toString())
        println(date.format("yyyy-MM-dd"))
        Date date1 = new Date()
        println date.compareTo(date1)
    }

    static void regex(){
        def regex = ~'G*'
        /**
         * 有两个特殊的位置字符用于表示一行的开始和结束：caret（∧）和美元符号（$）。

         正则表达式也可以包括量词。加号（+）表示一次或多次，应用于表达式的前一个元素。星号（*）用于表示零个或多个出现。问号（？）表示零或一次。

         元字符{和}用于匹配前一个字符的特定数量的实例。

         在正则表达式中，句点符号（。）可以表示任何字符。这被描述为通配符。

         正则表达式可以包括字符类。一组字符可以作为简单的字符序列，包含在元字符[和]中，如[aeiou]中。对于字母或数字范围，可以使用[a-z]或[a-mA-M]中的短划线分隔符。字符类的补码由方括号内的前导插入符号表示，如[∧a-z]中所示，并表示除指定的字符以外的所有字符。下面给出了正则表达式的一些示例。
         */
        'Groovy' =~ 'Groovy'
        'Groovy' =~ 'oo'
        'Groovy' ==~ 'Groovy'
        'Groovy' ==~ 'oo'
        'Groovy' =~ '∧G'
        'Groovy' =~ 'G$'
        'Groovy' =~ "Gro*vy"
        'Groovy' =~ 'Gro{2}vy'
    }

    static void tryCache(){
        try {
            def arr = new int[3];
            arr[5] = 5;
        } catch(Exception ex) {
            println("Catching the exception");
        }

    }



}
