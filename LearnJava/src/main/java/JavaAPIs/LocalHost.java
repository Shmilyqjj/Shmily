package JavaAPIs;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Shmily
 * @Description:
 * @CreateTime: 2024/8/20 下午10:21
 */

public class LocalHost {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostName();
        String hostAddress = localHost.getHostAddress();
        System.out.println("主机名：" + hostName);
        System.out.println("主机地址：" + hostAddress);
    }
}
