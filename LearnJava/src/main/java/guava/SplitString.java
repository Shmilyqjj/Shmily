package guava;

import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.Map;

/**
 *  Guava api string split
 */
public class SplitString {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        String str = "k1=vq,k2=v2,_tag_host_name=hhh,_tag_port=9003,client_ip=223.33.33.33,local_ip=192.168.1.2";
//        String str = "k1=vq,k2=v2,_tag_host_name=hhh,_tag_port=9003,client_ip=223.33.33.33, 192.168.1.2";
        try {
            map.putAll(Splitter.on(",").withKeyValueSeparator("=").split(str));
        } catch (Exception e) {
            System.out.println("err:" + e);
        }
        map.forEach((k, v) -> System.out.println(k + ":" + v));
    }
}
