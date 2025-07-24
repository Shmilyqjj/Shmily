
import ldap.LdapUserMgr;

/**
 * Description: ldap用户管理测试
 * CreateTime: 2025/7/1 17:21
 * Author Shmily
 */
public class TestLdapUserMgr {
    public static void main(String[] args) {
        boolean create = LdapUserMgr.createUser("qjj", "123456", 6666, 6666);
        if (create) {
            System.out.println("User created");
        }

        boolean modifyPassword = LdapUserMgr.modifyPassword("qjj", "1234567");
        if (modifyPassword) {
            System.out.println("Password modified");
        }

        System.out.println(LdapUserMgr.isUserExist("qjj"));
        System.out.println(LdapUserMgr.isUserExist("qjj1"));
    }
}
