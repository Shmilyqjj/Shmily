package zkClient;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;

/**
 * :Description: 生成zk超级用户信息和配置
 * :Create Time: 2021/12/30 0:15
 * :Site: shmily-qjj.top
 * @author 佳境Shmily
 */
public class GenZKSuperUser {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String idPassword = "super:superpw";
        String superDigest = DigestAuthenticationProvider.generateDigest(idPassword);
        System.out.println(String.format("superDigest: %s", superDigest));
        System.out.println(String.format("ZKServer Java配置: -Dzookeeper.DigestAuthenticationProvider.superDigest=%s", superDigest));
        System.out.println(String.format("ZKClient 如何使用:  addauth digest %s", idPassword));
    }
}
