package test;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import test.pojo.Student;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * :Description:第一个MyBatis程序
 * :Author: 佳境Shmily
 * :Create Time: 2022/3/8 22:24
 * :Site: shmily-qjj.top
 * MyBatis，是一个基于Java的持久层框架
 * ########################################################################
 * 持久层： 可以将业务数据存储到磁盘，具备长期存储能力，只要磁盘不损坏，在断电或者其他情况下，重新开启系统仍然可以读取到这些数据。
 * 优点： 可以使用巨大的磁盘空间存储相当量的数据，并且很廉价
 * 缺点：慢（相对于内存而言）
 *
 * 为什么使用 MyBatis
 * 在我们传统的 JDBC 中，我们除了需要自己提供 SQL 外，还必须操作 Connection、Statment、ResultSet，不仅如此，为了访问不同的表，不同字段的数据，我们需要些很多雷同模板化的代码，闲的繁琐又枯燥。
 * 而我们在使用了 MyBatis 之后，只需要提供 SQL 语句就好了，其余的诸如：建立连接、操作 Statment、ResultSet，处理 JDBC 相关异常等等都可以交给 MyBatis 去处理，我们的关注点于是可以就此集中在 SQL 语句上，关注在增删改查这些操作层面上。
 * 并且 MyBatis 支持使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。
 * 总结：1.SQL模板化，减少代码冗余，支持动态SQL拼接 2.无需关心Connection、Statment、ResultSet操作  3.XML简化配置，直接映射Java对象实体类
 *
 * 基本原理
 * 应用程序找 MyBatis 要数据
 * MyBatis 从数据库中找来数据
 * 1.通过 mybatis-config.xml 定位哪个数据库
 * 2.通过 Student.xml 执行对应的 sql 语句
 * 3.基于 Student.xml 把返回的数据库封装在 Student 对象中
 * 4.把多个 Student 对象装载一个 Student 集合中
 * 返回一个 Student 集合
 *
 * 关于 parameterType： 就是用来在 SQL 映射文件中指定输入参数类型的，可以指定为基本数据类型（如 int、float 等）、包装数据类型（如 String、Interger 等）以及用户自己编写的 JavaBean 封装类
 * 关于 resultType： 在加载 SQL 配置，并绑定指定输入参数和运行 SQL 之后，会得到数据库返回的响应结果，此时使用 resultType 就是用来指定数据库返回的信息对应的 Java 的数据类型。
 * 关于 “#{}” ： 在传统的 JDBC 的编程中，占位符用 “?” 来表示，然后再加载 SQL 之前按照 “?” 的位置设置参数。而 “#{}” 在 MyBatis 中也代表一种占位符，该符号接受输入参数，在大括号中编写参数名称来接受对应参数。当 “#{}” 接受简单类型时可以用 value 或者其他任意名称来获取。
 * 关于 “${}” ： 在 SQL 配置中，有时候需要拼接 SQL 语句（例如模糊查询时），用 “#{}” 是无法达到目的的。在 MyBatis 中，“${}” 代表一个 “拼接符号” ，可以在原有 SQL 语句上拼接新的符合 SQL 语法的语句。使用 “${}” 拼接符号拼接 SQL ，会引起 SQL 注入，所以一般不建议使用 “${}”。
 *
 */
public class TestMyBatis {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml"; // resource目录下
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 然后根据 sqlSessionFactory 得到 session
        SqlSession session = sqlSessionFactory.openSession();
        // 最后通过 session 的 selectList() 方法调用 sql 语句 listStudent
        List<Student> listStudent = session.selectList("listStudent");
        for (Student student : listStudent) {
            System.out.println("ID:" + student.getId() + ",NAME:" + student.getName());
        }
        System.out.println("=============================");
        CRUD(session);
        System.out.println("=============================");
        find(session);
        System.out.println("=============================");
        dynamicSelect(session);
        System.out.println("=============================");
        dynamicUpdate(session);
        System.out.println("=============================");
        select2ColumnAndReturnMap(session);
        // 关闭 session
        session.close();
    }


    /**
     * MyBatis实现增删改查
     * @param session
     */
    public static void CRUD(SqlSession session){
        // 增
        Student st = new Student();
        st.setId(5);
        st.setStudentID(5);
        st.setName("newStu");
        session.insert("addStudent", st); // 对应xml中insert id
        System.out.println("addStudent完成");

        // 查
        Student st1 = session.selectOne("getStudent", 5);  // selectOne只有一个参数
        System.out.println(String.format("%s %s %s", st1.getId(),st1.getStudentID(),st1.getName()));

        // 改
        st1.setName("newStudent");
        session.update("updateStudent", st1);
        System.out.println("updateStudent完成");

        // 删
        session.delete("deleteStudent", st1);
        System.out.println("deleteStudent完成");

        // 提交修改
        session.commit();

    }

    public static void find(SqlSession session){
        // selectList() 查询结果不止一个  用selectList
        List<Student> students = session.selectList("findStudentByName", "J");
        for (Student stud: students) {
            System.out.println(String.format("%s %s %s", stud.getId(),stud.getStudentID(),stud.getName()));
        }
    }

    /**
     * MyBatis 动态Select SQL
     */
    public static void dynamicSelect(SqlSession session){
        List<Student> students = session.selectList("dynamicSelectIf",new Student(1, 1, null));
        for (Student s:students) {
            System.out.println(String.format("%s %s %s", s.getId(), s.getStudentID(), s.getName()));
        }
        System.out.println("###############");
        List<Student> students1 = session.selectList("dynamicSelectChoose",new Student(3, 3, null));
        for (Student s1:students1) {
            System.out.println(String.format("%s %s %s", s1.getId(), s1.getStudentID(), s1.getName()));
        }
        System.out.println("###############");
        final List<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        ids.add(4);
        List<Student> students2 = session.selectList("dynamicSelectForeach",ids);
        for (Student s:students2) {
            System.out.println(String.format("%s %s %s", s.getId(), s.getStudentID(), s.getName()));
        }
    }

    /**
     * 查询两个字段，返回两个字段作为key和value的map
     */
    public static void select2ColumnAndReturnMap(SqlSession session){
        // 查询student表的id,name两个字段，返回id为key，name为value的map
        List<Map<String,Object>> select2ColumnAndReturnMap = session.selectList("Select2ColumnAndReturnMap");
        Map<Integer,String> result = new HashMap<Integer, String>();
        select2ColumnAndReturnMap.stream().forEach(x -> result.put(Integer.valueOf(String.valueOf(x.get("id"))), String.valueOf(x.get("name"))));
        for (Integer id:result.keySet()) {
            System.out.println("id:" + id + " name:" + result.get(id));
        }
    }

    /**
     * MyBatis 动态Update SQL
     */
    public static void dynamicUpdate(SqlSession session){
        session.update("dynamicUpdateSet", new Student(4,4 ,"Student"));
        List<Student> stu = session.selectList("dynamicSelectChoose",new Student(4, 4, null));
        for (Student s:stu) {
            System.out.println(String.format("%s %s %s", s.getId(), s.getStudentID(), s.getName()));
        }
    }

}
