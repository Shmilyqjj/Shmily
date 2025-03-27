import org.springframework.ldap.support.filter.EqualsFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.directory.InitialDirContext;

/**
 * @author 佳境Shmily
 * @Description: ldap验证用户登录
 * @CreateTime: 2025/3/3 17:54
 * @Site: shmily-qjj.top
 * @Usage:
     java -cp Openldap-1.0-jar-with-dependencies.jar LDAPTest \
     --urls "" \
     --base-dn "" \
     --username "" \
     --password "" \
     --identity-attribute "" \
     --email-attribute "" \
     --login-user "" \
     --login-password ""
 */
public class LDAPTest {
    private static Properties searchEnv;
    private static String baseDn;
    private static String ldapUserIdentifyingAttribute;
    private static String emailAttr;


    public static void main(String[] args) {
        Map<String, String> params = parseArgs(args);
        String urls = params.get("urls");
        baseDn = params.get("base-dn");
        String username = params.get("username");
        String password = params.get("password");
        ldapUserIdentifyingAttribute = params.get("identity-attribute");
        emailAttr = params.get("email-attribute");
        String loginUser = params.get("login-user");
        String loginUserPwd = params.get("login-password");
        System.out.printf("LdapConf==(urls:%s  base-dn:%s username:%s  password:%s  id-attr:%s  email-attr:%s)%n",
                urls, baseDn, username, password,ldapUserIdentifyingAttribute,emailAttr);
        System.out.printf("LoginUser==(user:%s password:%s)%n", loginUser, loginUserPwd);

        searchEnv = getManagerLdapEnv(urls, username, password);
        System.out.println(login(loginUser, loginUserPwd));
    }

    private static String login(String userId, String userPwd) {
        LdapContext ctx = null;
        try {
            // Connect to the LDAP server and Authenticate with a service user of whom we know the DN and credentials
            ctx = new InitialLdapContext(searchEnv, null);
            SearchControls sc = new SearchControls();
            sc.setReturningAttributes(new String[]{emailAttr});
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            EqualsFilter filter = new EqualsFilter(ldapUserIdentifyingAttribute, userId);
            NamingEnumeration<SearchResult> results = ctx.search(baseDn, filter.toString(), sc);
            if (results.hasMore()) {
                // get the users DN (distinguishedName) from the result
                SearchResult result = results.next();
                NamingEnumeration<? extends Attribute> attrs = result.getAttributes().getAll();
                System.out.println(attrs.hasMore());
                while (attrs.hasMore()) {
                    // Open another connection to the LDAP server with the found DN and the password
                    searchEnv.put(Context.SECURITY_PRINCIPAL, result.getNameInNamespace());
                    searchEnv.put(Context.SECURITY_CREDENTIALS, userPwd);
                    try {
                        new InitialDirContext(searchEnv);
                    } catch (Exception e) {
                        System.out.println("[WARN] invalid ldap credentials or ldap search error: " + e);
                        return null;
                    }
                    Attribute attr = attrs.next();
                    System.out.println(attr.get().toString());
                    if (attr.getID().equals(emailAttr)) {
                        return (String) attr.get();
                    }
                }
            } else {
                System.out.println("no results");
            }
        } catch (NamingException e) {
            System.err.println("[ERROR] ldap search error" + e);
            return null;
        } finally {
            try {
                if (ctx != null) {
                    ctx.close();
                }
            } catch (NamingException e) {
                System.err.println("[ERROR] ldap context close error" + e);
            }
        }

        return null;
    }

    /**
     * 解析命令行参数
     */
    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    params.put(key, args[++i]);
                } else {
                    params.put(key, "");
                }
            }
        }
        return params;
    }

    private static Properties getManagerLdapEnv(String ldapUrls, String ldapSecurityPrincipal, String ldapPrincipalPassword) {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapSecurityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, ldapPrincipalPassword);
        env.put(Context.PROVIDER_URL, ldapUrls);
        return env;
    }
}
