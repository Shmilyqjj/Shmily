package basic.grammar

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class TestGroovyXML {
    static void main(String[] args) {
        def jsonSlurper = new JsonSlurper()
        def object = jsonSlurper.parseText('{ "name": "John", "ID" : "1"}')
        println(object.name);
        println(object.ID);

        Object lst = jsonSlurper.parseText('{ "List": [2, 3, 4, 5] }')
        lst.each { println it }

        // 输出json串
        def output = JsonOutput.toJson([name: 'John', ID: 1])
        println(output);
    }
}
