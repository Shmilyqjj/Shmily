package com.study.spark.util.Tests;
import com.study.spark.util.Bean.CategoryClickCount;
import com.study.spark.util.HbaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Spark爱奇艺项目 数据操作类DAO Java
 * 查询方法query
 */

public class JAVATest_CategoryClickCountDAO {

    //查询 query方法 按day查询
    public List<CategoryClickCount> query(String day) throws Exception{
        List<CategoryClickCount>  list = new ArrayList<>();
        Map<String,Long> map = HbaseUtils.getInstance().query("category_clickcount",day); //按day查找category_clickcount表中的字段存入Map
        //遍历Map,将值存入Bean
        for (Map.Entry<String,Long>entry:map.entrySet()){
            CategoryClickCount model = new CategoryClickCount();
            model.setName(entry.getKey());
            model.setValue(entry.getValue());
            list.add(model);
        }
        return list;
    }



    //测试
    public static void main(String[] args) throws Exception {
        JAVATest_CategoryClickCountDAO dao = new JAVATest_CategoryClickCountDAO();
        List<CategoryClickCount> list = dao.query("2019");
        for (CategoryClickCount c : list){
            System.out.println(c.getValue());
        }
    }




}
