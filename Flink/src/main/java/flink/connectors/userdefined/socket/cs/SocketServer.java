package flink.connectors.userdefined.socket.cs;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/10/22 15:53
 * @Site: shmily-qjj.top
 */
public class SocketServer {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            //从请求队列中取出链接
            socket = serverSocket.accept();
            //获取客户端信息
            inputStream = socket.getInputStream();
            //回复客户端
            outputStream = socket.getOutputStream();
            //字节数组
            byte[] bytes = new byte[1024];
            //字节数组长度
            int len;
            boolean isLogin = false;
            while(true){
                //读取客户端请求信息
                while (inputStream.available() != 0 && (len = inputStream.read(bytes)) != -1) {
                    //接收到的消息
                    String s = new String(bytes, "utf-8");
                    //这里你可以根据你的实际消息内容做相应的逻辑处理
                    System.out.println("服务端收到客户端消息:" + s);
                    if(s.contains("链接上了")){
                        isLogin = true;
                    }
                }
                if(isLogin){
                    Scanner in = new Scanner(System.in);
                    if (in.hasNext()){
                        String inStr = in.nextLine();
                        System.out.println("输入数据：" + inStr);
                        outputStream.write(inStr.getBytes(StandardCharsets.UTF_8));
                    }

                    Thread.sleep(1000);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


}
