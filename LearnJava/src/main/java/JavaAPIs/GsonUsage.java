package JavaAPIs;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/12/18 12:26
 * @Site: shmily-qjj.top
 */
public class GsonUsage {
    private static transient Gson gson;

    public static void main(String[] args) throws IOException {
        gson =  new GsonBuilder()
//                .setLenient()// json宽松
                .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
//                .serializeNulls() //智能null
//                .setPrettyPrinting()// 调教格式
                .disableHtmlEscaping() //默认是GSON把HTML 转义的
                .create();

        // 1.基本数据类型解析与生成
        System.out.println(gson.fromJson("100", int.class));
        System.out.println(gson.fromJson("\"99.99\"", double.class));
        System.out.println(gson.fromJson("true", boolean.class));
        System.out.println(gson.fromJson("String", String.class));
        String jsonNumber = gson.toJson(100);
        String jsonBoolean = gson.toJson(false);
        String jsonString = gson.toJson("String");
        System.out.println(jsonNumber + " " + jsonBoolean + " " + jsonString);

        // 2. Java Object 序列化与反序列化
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        User userObj = new User("qjj", 24, true);
        // 序列化
        String userJson = gson.toJson(userObj);
        System.out.println(userJson);
        // 反序列化
        User user = gson.fromJson(userJson, User.class);
        System.out.println(user.toString());
        // null的处理
        User userObjWithNull = new User(null, 24, true);
        System.out.println(gson.toJson(userObjWithNull));  // 默认自动忽略null的字段，只返回非null值的字段 如果gson初始化时设置了serializeNulls()则不忽略null字段
        // 反序列化array
        String namesJson = "['aaaa','bbbb','cccc']";
        String[] namesArray = gson.fromJson(namesJson, String[].class);
        System.out.println(Arrays.toString(namesArray));
        // 反序列化list
        String listJson = "[{'isDeveloper':false,'name':'xiaoqiang','age':26,'email':'578570174@qq.com'},{'isDeveloper':true,'name':'xiaoqiang123','age':27,'email':'578570174@gmail.com'}]";
        Type userListType = new TypeToken<ArrayList<User>>(){}.getType();  // 对于List，反序列化时必须提供它的Type，通过Gson提供的TypeToken<T>.getType()方法可以定义当前List的Type。
        List<User> userList = gson.fromJson(listJson, userListType);
        System.out.println(userList);
        // 嵌套Json反序列化
        System.out.println("========================================================");
        String nestedJson = "{\"k1\": \"v1\",\"k2\": \"v2\", \"k3\": {\"v3k1\": \"v3v1\", \"v3k2\": \"v3v2\"}, \"k4\": [{\"k4k1\": \"k4v1\"},{\"k4k2\": \"k4v2\"}]}\n";
        System.out.print(nestedJson);
        JsonObject jo = JsonParser.parseString(nestedJson).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if(value.isJsonPrimitive()){
                // k :v 形式
                System.out.println("[JsonPrimitive] k=" + key + " v=" + value.getAsString());
            }else if(value.isJsonArray()){
                // []形式
                System.out.println("[JsonArray] k=" + key + " value is JsonArray. Value: " + value.toString());
                value.getAsJsonArray().forEach(ee -> ee.getAsJsonObject().entrySet().forEach(System.out::println));
            }else if(value.isJsonObject()){
                // Json嵌套 形式
                System.out.println("[JsonObject] k=" + key + " value is jsonObject. Value: " + value.toString());
                value.getAsJsonObject().entrySet().forEach(e -> System.out.println(e.getKey() + " " + e.getValue()));
            }else {
                // JsonNull 形式
                System.out.println("[JsonNull] k=" + key + " value is JsonNull");
                System.out.println(value.isJsonNull());
            }
        }


        // 3. JsonWriter
        Map<String,String> m = new HashMap<>(4);
        m.put("a", "aa");
        m.put("b", "bb");
        m.put("c", "cc");
        m.put("d", "dd");
        List<String> l = new ArrayList<>();
        l.add("a");
        l.add("b");
        l.add("c");
        l.add("d");
        StringBuilderWriter ext = new StringBuilderWriter();
        JsonWriter writer = new JsonWriter(ext);
        writer.beginObject();
        writer.name("json_data").jsonValue(gson.toJson(m));
        writer.name("key").value("value");
        writer.name("array");
        writer.beginArray();
        for (String s : l) {
            writer.value(s);
        }
        writer.endArray();
        writer.endObject();
        writer.close();
        A a = new A();
        a.je = JsonParser.parseString(ext.toString());
        System.out.println(gson.toJson(a));


        // 4. List<Object> 序列化与反序列化
        List<Object> lo = new ArrayList<>();
        lo.add("a");
        lo.add(1);
        lo.add("b");
        lo.add(2);
        lo.add(3.1);
        lo.add(4L);
        lo.add('c');
        String value = gson.toJson(lo);
        System.out.println(value);
        List list = gson.fromJson(value, List.class);
        List<Object> anotherList = new ArrayList<Object>(list);
        anotherList.forEach(System.out::println);
//        List<Object> lo1 = gson.fromJson(value, List.class);
//        lo1.forEach(System.out::println);
    }


    static class A {
        public JsonElement je;

    }

    static class User{
        @SerializedName("fullName") // 控制序列化/反序列化 的变量名称
        private String name;
        @SerializedName(value = "age", alternate = "user_age")  // 如果JSON传的是age，那么就用age的值，如果传的是user_age，那么就用user_age的值。
        private int age;
        private boolean blocked;
        @Expose(serialize = false, deserialize = false)  // 不参与序列化与反序列化的字段
        private String doNotSerialize;
        private transient String doNotSerialize1; // transient修饰的变量 不参与序列化与反序列化的字段

        public User(String name, int age, boolean blocked) {
            this.name = name;
            this.age = age;
            this.blocked = blocked;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", blocked=" + blocked +
                    '}';
        }
    }
}
