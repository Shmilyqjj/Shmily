package basic.grammar
/**
 * Groovy在使用I / O时提供了许多辅助方法，Groovy提供了更简单的类来为文件提供以下功能。

 读取文件
 写入文件
 遍历文件树
 读取和写入数据对象到文件
 除此之外，您始终可以使用下面列出的用于文件I / O操作的标准Java类。

 java.io.File
 java.io.InputStream
 java.io.OutputStream
 java.io.Reader
 java.io.Writer
 */
class TestGroovyIO {
    static void main(String[] args){
        String filePath = "/tmp/test.txt"
        writeToFile(filePath)
        readFile(filePath)
        getFileSize(filePath)
        isDir(filePath)
        isDir('/tmp')
        mkdir("/tmp/qjj/test")
        delete("/tmp/qjj")
        getList("/tmp/")
        getRecurseList("/opt/Env/groovy-3.0.8")
    }

    static void readFile(String filePath){
        //按行读
//        new File(filePath).eachLine {it -> println(it)}
        def file = new File(filePath)
        file.eachLine {println(it)}
        println("----------------")
        //读取整个文件
        println(file.text)
    }

    static void writeToFile(String filePath){
        def file = new File(filePath)
        // 覆盖写入一行
        file.withWriter('utf-8'){
            writer -> writer.writeLine("Test Groovy")
        }
        // 追加写入一行日期
        file.append(new Date().format('yyyy-MM-dd'))
    }

    static void getFileSize(String filePath){
        File file = new File(filePath)
        println file.size() + " bytes"
    }

    static void isDir(String path){
        //判断是否是目录
        def file = new File(path)
        println(String.format("Is %s directory? %s", path, file.isDirectory()))
    }

    static void mkdir(String dirPath){
        // 没有目录会创建  目录已存在不会报错
        def dir = new File(dirPath)
//        dir.mkdir()  //不会递归创建 类似mkdir  如果上层目录不存在，不会报错也不会创建
        dir.mkdirs()  //会递归创建 mkdir -p
    }

    static void delete(String path){
        def file = new File(path)
        if(file.exists()){
            file.deleteOnExit()
        }
        if(file.isDirectory()){
            file.deleteDir()
        }else if(file.isFile()){
            file.delete()
        }
    }

    static void getList(String dirPath){
        //ls
        def ls = new File(dirPath).listFiles()
        ls.each {file -> println(file.absolutePath)}  //列出目录下的文件和目录
        new File(dirPath).eachFile {println(it.absolutePath)} //列出文件
        new File(dirPath).eachDir {println(it.absolutePath)}  //列出目录
    }

    static void getRecurseList(String dirPath){
        def ls = new File(dirPath)
        ls.eachFileRecurse {file -> println(file.getAbsolutePath())}
    }


}
