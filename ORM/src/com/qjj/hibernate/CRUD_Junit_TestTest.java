package com.qjj.hibernate;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
/**  在需要进行单元测试的类中，使用快捷键alt+insert，选择JUnit test，选择JUnit4。 */
public class CRUD_Junit_TestTest {
    Configuration cfg = new Configuration().configure();
    StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure().build();
    SessionFactory sessionFactory  = cfg.buildSessionFactory(standardRegistry);
    //创建session对象
    Session session = sessionFactory.getCurrentSession();
    //开始事务
    Transaction transaction = session.beginTransaction();
    News news = new News();


    @Test
    //HQL查询  Hibernate Query Language
    public void HQLTest() {

        //1.检索News表中所有字段的值
//        Query query = session.createQuery("from News");
//        List News = query.list();
//        for (int i = 0; i < News.size(); i++) {
//            news = (News)News.get(i);
//            System.out.println(i+"--"+news.getAuthor());
//        }


//        //2.检索News表中某个字段的值  select distinct去除重复字段值
        Query query = session.createQuery("Select n.id from News n");
        List ids = query.list();
        int id = 0;
        for (int i = 0; i < ids.size(); i++) {
            id = (int)ids.get(i);
            System.out.println(i+"->"+id);
        }

//         //3.查询两个以上的属性，查询的结果会以数组的方式返回
//          Query q = session.createQuery("select n.date,n.author from News n");
//          List news = q.list();
//        for (int i = 0; i < news.size(); i++) {
//            Object o[] = (Object[]) news.get(i);//对象数组
//            System.out.println(o[0]+"--"+o[1]);
//        }


//          //4.使用函数
//            Query query = session.createQuery("Select count(*) from News n");
//            List count = query.list();
//            Object c = 0;
//            for (int i = 0; i < count.size(); i++) {
//            c = (Object)count.get(i);
//            }
//            System.out.println("count:"+c);

//        //5.使用Where子句  可以使用where子句来限定查询的条件，除了 = 运算之外，还有 >、>=、<、<=、!= 或 <>等比较运算  between/in/not in/like/not like
//        Query query = session.createQuery("from News n where n.id=3");
//        List oneResult = query.list();
//        for (int i = 0; i < oneResult.size(); i++) {
//            News n = (News) oneResult.get(i);
//            System.out.println("id为3的数据为："+n.getTitle()+" -- "+n.getAuthor()+" -- "+n.getDate());
//        }

//        //6.hibernate的HQL支持嵌套查询
//        Query q = session.createQuery("from News n where n.id>(select avg(id) from News )");
//        List news = q.list();
//        for (int i = 0; i < news.size(); i++) {
//            News n = (News)news.get(i);
//            System.out.println(n.getAuthor()+n.getId()+n.getTitle());
//        }

          //7.HQL也支持order by group by having+

//          //8.HQL语句使用"?"占位符  然后使用 query对象的srtXXX方法给?绑定参数   不光可以用?  也可以用命名参数  比如name  author=:name setString("name","xx")
//        Query q = session.createQuery("from News where author=:name");
////        q.setString(0,"qjj");
//        q.setString("name","qjj");
//        List l = q.list();
//        for (int i = 0; i < l.size(); i++) {
//            news = (News)l.get(i);
//            System.out.println(news.getDate());
//        }

//        //9.HQL的update
//        Query q = session.createQuery("update News  set author='zxw' where id=4");
//        q.executeUpdate();


//        //10.HQL的delete
//        Query q = session.createQuery("delete News where id = 5");
//        q.executeUpdate();
    }


    @Test
    public void QBCTest(){
        //...........
    }

    @Test
    public void NativeSqlTest(){

        //1.sql语句直接查询
        String sql = "select * from News";
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        //将表名或者表的别名与实体类关联起来
        sqlQuery.addEntity("News",News.class);
        List l = sqlQuery.list();
        for (int i = 0; i < l.size(); i++) {
            news = (News)l.get(i);
            System.out.println(news.getId()+"---"+news.getAuthor()+"---"+news.getTitle());
        }
    }



}
