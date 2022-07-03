/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/7/3 23:26
 * @Site: shmily-qjj.top
 */
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class LdapDemo{
    public static void main(String[] args){
        System.out.println("IPAdress: " + args[0]);
        System.out.println("Username: " + args[1]);
        System.out.println("Password: " + args[2]);
        Hashtable<String, String> tbl = new Hashtable<String, String>(4);
        String LDAP_URL = "ldap://" +args[0]+ "/dc=shmily-qjj,dc=top";
        tbl.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        tbl.put(Context.PROVIDER_URL, LDAP_URL);
        tbl.put(Context.SECURITY_AUTHENTICATION, "simple");
        tbl.put(Context.SECURITY_PRINCIPAL, args[1]);
        tbl.put(Context.SECURITY_CREDENTIALS, args[2]);
        System.out.println("env setting");
        DirContext context = null;
        try {
            System.out.println("login verification begins...");
            context = new InitialDirContext(tbl);
            System.out.println("login successfully.");
        } catch (Exception ex) {
            System.out.println("login failed.");
        } finally {
            try {
                if (context != null) {
                    context.close();
                    context = null;
                }
                tbl.clear();
            } catch (Exception e) {
                System.out.println("exception happened.");
            }
        }
    }
}
