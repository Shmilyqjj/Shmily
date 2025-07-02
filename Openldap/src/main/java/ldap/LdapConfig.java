package ldap;

import javax.naming.Context;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import java.util.Hashtable;

public class LdapConfig {
    private static final String LDAP_URL = "ldap://shmily:389";
    private static final String ADMIN_DN = "cn=admin,dc=shmily-qjj,dc=top";
    private static final String ADMIN_PASSWORD = "123456";
    public static final String USER_BASE_DN = "ou=People,dc=shmily-qjj,dc=top";

    public static DirContext getContext() throws Exception {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, LDAP_URL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ADMIN_DN);
        env.put(Context.SECURITY_CREDENTIALS, ADMIN_PASSWORD);
        return new InitialLdapContext(env, null);
    }
}