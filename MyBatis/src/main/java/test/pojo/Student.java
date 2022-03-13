package test.pojo;

/**
 * :Description: Student实体类
 * :Author: 佳境Shmily
 * :Create Time: 2022/3/11 21:09
 * :Site: shmily-qjj.top
 */

public class Student {
    int id;
    int studentID;
    String name;

    public Student(){};

    public Student(int id, int studentID, String name){
        this.id = id;
        this.studentID = studentID;
        this.name = name;
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}