package Reflect;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AutoGetClassesInJar {
    public static void main(String[] args) throws IOException {
        AutoGetClassesInJar testAutoGetClasses = new AutoGetClassesInJar();
        testAutoGetClasses.getFunctionClasses().forEach(System.out::println);
    }

    private List<Class<?>> getFunctionClasses() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        String classResource = this.getClass().getName().replace(".", "/") + ".class";  //  package_name/class_name.class
        String classUrl = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(classResource)).getFile();
        System.out.println(classUrl);  // .class file absolute path
        String jarLocation = "file://" + classUrl.replace(classResource, "") + "LearnJava-1.0-SNAPSHOT.jar";
//        jarLocation = "file:///home/shmily/MyProjects/Shmily/LearnJava/target/LearnJava-1.0-SNAPSHOT.jar";
        jarLocation = new URL(jarLocation).getFile();
        System.out.println(jarLocation);

        // 获取jar中所有class 包名.类名
        ZipInputStream zip = new ZipInputStream(Files.newInputStream(Paths.get(jarLocation)));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                String className = entry.getName().replace("/", "."); // This still has .class at the end
                className = className.substring(0, className.length() - 6); // remvove .class from end
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    System.out.println("Could not load class:" + className + " Exception: " + e);
                }
            }
        }
        return classes;
    }
}
