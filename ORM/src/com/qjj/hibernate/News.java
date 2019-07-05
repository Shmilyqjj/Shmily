package com.qjj.hibernate;

import java.sql.Date;

public class News {
    private Integer id;
    private String title;
    private String author;
    private Date date;

    public News() {
        super();
        // TODO Auto-generated constructor stub
    }

    public News(Integer id, String title, String author, Date date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.date = date;
    }
    public News( String title, String author, Date date) {
        this.title = title;
        this.author = author;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}

