package test.pojo;

/**
 * @author 佳境Shmily
 * @Description: User实体类
 * @CreateTime: 2024/1/3 下午2:19
 * @Site: shmily-qjj.top
 */

public class User {
    String id;
    String name;
    Integer age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
