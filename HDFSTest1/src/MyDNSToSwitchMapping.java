import org.apache.hadoop.net.DNSToSwitchMapping;

import java.util.ArrayList;
import java.util.List;

public class MyDNSToSwitchMapping implements DNSToSwitchMapping{
    // 传递的是客户端的ip列表，返回机架感知的路径列表
    public List<String> resolve(List<String> names) {
        ArrayList<String> lists = new ArrayList<String>();
        if (names != null && names.size() > 0) {
            for (String name : names) {
                int ip = 0;
                //1 获取ip地址
                if (name.startsWith("hadoop")) {
                    String no = name.substring(6);
                    // hadoop102
                    ip = Integer.parseInt(no);
                } else if (name.startsWith("192")) {
                    // 192.168.1.102
                    ip = Integer.parseInt(name.substring(name.lastIndexOf(".") + 1));
                }
                //2 定义机架
                if (ip < 104) {
                    lists.add("/rack1/" + ip);
                } else {
                    lists.add("/rack2/" + ip);
                }
            }
        }
        return lists;
    }
    public void reloadCachedMappings() {
    }
    public void reloadCachedMappings(List<String> names) {
    }
}
