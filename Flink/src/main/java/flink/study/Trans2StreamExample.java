package flink.study;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import javax.xml.crypto.Data;

/**
 * :Description: 对象 转换成流 计算 示例
 * :Author: 佳境Shmily
 * :Create Time: 2022/4/6 14:31
 * :Site: shmily-qjj.top
 */
public class Trans2StreamExample {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Person> flintstones = env.fromElements(
                new Person("Fred", 35),
                new Person("Wilma", 18),
                new Person("Pebbles", 2));
        DataStream<Person> adults = flintstones.filter(new FilterFunction<Person>() {
            @Override
            public boolean filter(Person person) throws Exception {
                return person.age >= 18;
            }
        });

        adults.print();
        System.out.println(env.getExecutionPlan());
        env.execute();

    }


    public static class Person {
        public String name;
        public Integer age;
        public Person() {}

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            // Datastream的print()方法调用
            return this.name.toString() + ": age " + this.age.toString();
        }
    }

}

