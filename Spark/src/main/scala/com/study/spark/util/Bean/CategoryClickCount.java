package com.study.spark.util.Bean;

public class CategoryClickCount {
    private String name; //类别名
    private Long value; //次数

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
