import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        List<Field> cols = new ArrayList<>();
        cols.add(new Field("a","A"));
        cols.add(new Field("b","x"));
        cols.add(new Field("c","C"));
        cols.add(new Field("d","y"));
        List<Field> lo = cols.stream().map(col -> {
            if ("b".equals(col.getName())) {
                col.setComment("B");
            }
            if ("d".equals(col.getName())) {
                col.setComment("D");
            }
            return col;
        }).collect(Collectors.toList());

        lo.forEach(System.out::println);
    }


}

class Field {
    String name;
    String comment;

    public Field(String name, String comment){
        this.name = name;
        this.comment = comment;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
