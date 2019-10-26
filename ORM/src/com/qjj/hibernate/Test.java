package com.qjj.hibernate;
import java.sql.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;


public class Test {
    public static void main(String[] args){
        //创建一个会话工厂对象SessionFactory
        SessionFactory sessionFactory = null;
        //创建Config对象：对应hibernate的基本配置信息和对象关系映射 Configuration 类负责管理Hibernate 的配置信息。启动Hibernate、创建SessionFactory对象。
        Configuration config = new Configuration().configure();
        //创建一个SessionFactoryRegistry对象hibernate的任何配置和服务都需要在该对象中注册后才有用
        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure().build();
        sessionFactory  = config.buildSessionFactory(standardRegistry);



        //创建并打开会话
        Session session =sessionFactory.getCurrentSession();
        //开启事务
        Transaction transaction = session.beginTransaction();
        //执行保存操作   瞬时对象创建后通过save方法或者saveOrUpdate方法进行持久化操作时才转变为持久化对象
        News news = new News("Hibernate", "qjj", new Date(new java.util.Date().getTime()));
        session.save(news); //save方法是瞬时对象变持久对象
        //提交持久化事务
        transaction.commit();
        //事务回滚
//        transaction.rollback();
        //关闭session - 使用OpenSession方法时需要手动关闭session  getCurrentSession方法是可以系统自动关闭session的
        session.close();

        //关闭sessionfactory
        sessionFactory.close();
    }

}

