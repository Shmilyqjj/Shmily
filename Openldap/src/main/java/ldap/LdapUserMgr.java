package ldap;

import javax.naming.NamingException;
import javax.naming.directory.*;

/**
 * Description:
 * CreateTime: 2025/7/1 17:15
 * Author Shmily
 */
public class LdapUserMgr {
    /**
     * 创建LDAP用户
     * @param uid 用户ID
     * @param password 初始密码
     * @param uidNumber linux用户id
     * @param gidNumber linux组id
     * @return 是否创建成功
     */
    public static boolean createUser(String uid, String password, int uidNumber, int gidNumber) {
        DirContext ctx = null;
        try {
            ctx = LdapConfig.getContext();

            // 创建用户属性
            Attributes attrs = new BasicAttributes();

            // 对象类
            Attribute objClasses = new BasicAttribute("objectClass");
            objClasses.add("account");
            objClasses.add("posixAccount");
            objClasses.add("top");
            objClasses.add("shadowAccount");
//            objClasses.add("inetOrgPerson");
            attrs.put(objClasses);

            // 用户属性
            attrs.put("uid", uid);
            attrs.put("cn", uid);
//            attrs.put("mail", mail);
            attrs.put("userPassword", password);
            attrs.put("loginShell", "/bin/bash");
            attrs.put("uidNumber", String.valueOf(uidNumber));
            attrs.put("gidNumber", String.valueOf(gidNumber));
            attrs.put("homeDirectory", "/home/" + uid);

            // 创建DN
            String userDn = "uid=" + uid + "," + LdapConfig.USER_BASE_DN;

            // 创建用户条目
            ctx.createSubcontext(userDn, attrs);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 修改用户密码
     * @param uid 用户ID
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    public static boolean modifyPassword(String uid, String newPassword) {
        DirContext ctx = null;
        try {
            ctx = LdapConfig.getContext();

            // 构造用户DN
            String userDn = "uid=" + uid + "," + LdapConfig.USER_BASE_DN;

            // 准备修改操作
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(
                    DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute("userPassword", newPassword)
            );

            // 执行修改
            ctx.modifyAttributes(userDn, mods);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 判断LDAP用户是否存在
     * @param uid 用户ID
     * @return true表示用户存在，false表示用户不存在或发生异常
     */
    public static boolean isUserExist(String uid) {
        DirContext ctx = null;
        try {
            ctx = LdapConfig.getContext();

            // 构造用户DN
            String userDn = "uid=" + uid + "," + LdapConfig.USER_BASE_DN;

            // 尝试获取用户属性
            Attributes attrs = ctx.getAttributes(userDn);

            // 如果能获取到属性，说明用户存在
            return attrs != null && attrs.size() > 0;

        } catch (NamingException e) {
            // 如果捕获到NameNotFoundException，说明用户不存在
            if (e instanceof javax.naming.NameNotFoundException) {
                return false;
            }
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除LDAP user
     * @param uid 要删除的user
     * @return 删除是否成功
     */
    public static boolean deleteUser(String uid) {
        DirContext ctx = null;
        try {
            ctx = LdapConfig.getContext();
            String userDn = "uid=" + uid + "," + LdapConfig.USER_BASE_DN;
            ctx.destroySubcontext(userDn);
            return true;
        } catch (Exception e) {
            System.err.println("删除LDAP条目失败: " + e.getMessage());
            return false;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    System.err.println("关闭LDAP连接时出错: " + e.getMessage());
                }
            }
        }
    }

}
