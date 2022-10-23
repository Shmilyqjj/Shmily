package flink.connectors.userdefined.socket.cs;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/10/22 15:51
 * @Site: shmily-qjj.top
 */
public class SocketClient {

    public static void main(String[] args) {
        try {
            //你的ip，你的端口
            Socket socket = new Socket("localhost", 9999);
            //获取输出流
            OutputStream outputStream = socket.getOutputStream();
            //发送链接成功的消息
            outputStream.write("客户端链接上了".getBytes());
            //获取输入流
            InputStream is = socket.getInputStream();
            byte[] inputBytes = new byte[1024];
            int len;
            //监听输入流,持续接收
            while(true){
                while (is.available() != 0 && (len = is.read(inputBytes)) != -1 ) {
                    //消息体
                    String s = new String(inputBytes, "utf-8");
                    System.out.println("客户端接收到消息:" + s);
                    //下边可以对接收到的消息进行处理
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
