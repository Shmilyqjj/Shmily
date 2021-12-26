package faceObjects;

/**
 * 单向引用和双向引用
 */
public class BetweenObjets {
    public static void main(String[] args) {
    Hero h1 = new Hero();
    h1.setName("h1h1");
    h1.setAge(50);
    Weapon w1 = new Weapon();
    w1.setName("w1w1");
    w1.setLevel(2);
    h1.setW(w1);//英雄h1使用武器w1  单向引用

    w1.setH(h1);//英雄h1使用武器w1，武器w1被英雄h1引用  双向引用
    System.out.println("我是"+h1.getName()+"我用"+h1.getW().getName());
    }
}

class Hero{
    private String name;
    private int age;
    private Weapon w;//faceObjects.Weapon 对象  代表英雄使用的武器

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Weapon getW() {
        return w;
    }

    public void setW(Weapon w) {
        this.w = w;
    }
}

class Weapon{
    private String name;
    private int level;
    private Hero h;//faceObjects.Hero 对象h 代表用这个武器的英雄

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Hero getH() {
        return h;
    }

    public void setH(Hero h) {
        this.h = h;
    }



}