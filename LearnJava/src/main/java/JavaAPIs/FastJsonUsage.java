package JavaAPIs;

/**
 * :Description: alibaba fastjson使用
 * :Author: 佳境Shmily
 * :Create Time: 2020/11/29 10:16
 * :Site: shmily-qjj.top
 * 方便的实现json对象与JavaBean对象的转换，实现JavaBean对象与json字符串的转换，实现json对象与json字符串的转换。
 * 除了这个fastjson以外，还有Google开发的Gson包，其他形式的如net.sf.json包，都可以实现json的转换
 *
 * 常用API
 * public static final Object parse(String text); // 把JSON文本parse为JSONObject或者JSONArray
 * public static final JSONObject parseObject(String text)； // 把JSON文本parse成JSONObject
 * public static final <T> T parseObject(String text, Class<T> clazz); // 把JSON文本parse为JavaBean
 * public static final JSONArray parseArray(String text); // 把JSON文本parse成JSONArray
 * public static final <T> List<T> parseArray(String text, Class<T> clazz); //把JSON文本parse成JavaBean集合
 * public static final String toJSONString(Object object); // 将JavaBean序列化为JSON文本
 * public static final String toJSONString(Object object, boolean prettyFormat); // 将JavaBean序列化为带格式的JSON文本
 * public static final Object toJSON(Object javaObject); //将JavaBean转换为JSONObject或者JSONArray。
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class FastJsonUsage {
    public static void main(String[] args) {
        String json = "[{\"class\":\"org.apache.spark.sql.catalyst.plans.logical.Filter\",\"num-children\":1,\"condition\":[{\"class\":\"org.apache.spark.sql.catalyst.expressions.Not\",\"num-children\":1,\"child\":0},{\"class\":\"org.apache.spark.sql.catalyst.expressions.InSubquery\",\"num-children\":2,\"values\":[0],\"query\":1},{\"class\":\"org.apache.spark.sql.catalyst.analysis.UnresolvedAttribute\",\"num-children\":0,\"nameParts\":\"[cust_no]\"},{\"class\":\"org.apache.spark.sql.catalyst.expressions.ListQuery\",\"num-children\":0,\"plan\":[{\"class\":\"org.apache.spark.sql.catalyst.plans.logical.GlobalLimit\",\"num-children\":1,\"limitExpr\":[{\"class\":\"org.apache.spark.sql.catalyst.expressions.Literal\",\"num-children\":0,\"value\":\"3\",\"dataType\":\"integer\"}],\"child\":0},{\"class\":\"org.apache.spark.sql.catalyst.plans.logical.LocalLimit\",\"num-children\":1,\"limitExpr\":[{\"class\":\"org.apache.spark.sql.catalyst.expressions.Literal\",\"num-children\":0,\"value\":\"3\",\"dataType\":\"integer\"}],\"child\":0},{\"class\":\"org.apache.spark.sql.catalyst.plans.logical.Project\",\"num-children\":1,\"projectList\":[[{\"class\":\"org.apache.spark.sql.catalyst.analysis.UnresolvedAlias\",\"num-children\":1,\"child\":0},{\"class\":\"org.apache.spark.sql.catalyst.expressions.Literal\",\"num-children\":0,\"value\":\"1\",\"dataType\":\"integer\"}]],\"child\":0},{\"class\":\"org.apache.spark.sql.catalyst.analysis.UnresolvedRelation\",\"num-children\":0,\"tableIdentifier\":{\"product-class\":\"org.apache.spark.sql.catalyst.TableIdentifier\",\"table\":\"bbb\"}}],\"children\":[],\"exprId\":{\"product-class\":\"org.apache.spark.sql.catalyst.expressions.ExprId\",\"id\":0,\"jvmId\":\"24b2d349-197c-4181-a687-e54e2f8398e6\"},\"childOutputs\":[]}],\"child\":0},{\"class\":\"org.apache.spark.sql.catalyst.analysis.UnresolvedRelation\",\"num-children\":0,\"tableIdentifier\":{\"product-class\":\"org.apache.spark.sql.catalyst.TableIdentifier\",\"table\":\"aaa\"}}]";
        JSONObject jsonObject = (JSONObject)JSON.parseObject(json);



    }
}
