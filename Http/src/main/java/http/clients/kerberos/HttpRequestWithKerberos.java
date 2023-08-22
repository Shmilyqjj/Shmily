package http.clients.kerberos;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.security.auth.module.Krb5LoginModule;
import http.util.HttpUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SaslRpcServer;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;
import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.ws.rs.core.Cookie;

import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class HttpRequestWithKerberos {
    public static void main(String[] args) throws IOException, LoginException {
        String krb5Path = "/etc/krb5.conf";
        String principal = "presto/HOST@xldw.xunlei.com";
        String keytabPath = "/etc/ecm/presto-conf/presto.keytab";

        //Java api login
//        loginWithJavaApi(keytabPath, principal);

        System.out.println("#################Start My code: #################");
        httpClientKerberos(krb5Path, principal, keytabPath);
        System.out.println("#################End My code: #################");

//        System.out.println("#################jersey code: #################");
//        jerseyClientKerberos(krb5Path, principal, keytabPath);

        //Ranger实现
//        System.out.println("#################Ranger code: #################");
//        rangerRestImpl(krb5Path, principal, keytabPath);




    }

    protected static WebResource setQueryParams(WebResource webResource, Map<String, String> params) {
        WebResource ret = webResource;
        if (webResource != null && params != null) {
            Set<Map.Entry<String, String>> entrySet= params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                ret = ret.queryParam(entry.getKey(), entry.getValue());
            }
        }
        return ret;
    }

    public static void loginWithJavaApi(String keytabPath, String principal) throws LoginException {
        System.out.println("===========Start login kerberos with java api===========");
        final Subject subject = new Subject();
        final Krb5LoginModule krb5LoginModule = new Krb5LoginModule();
        final Map<String,String> optionMap = new HashMap<>();
        optionMap.put("keyTab", keytabPath);
        optionMap.put("principal", principal);
        optionMap.put("doNotPrompt", "true");
        optionMap.put("refreshKrb5Config", "true");
        optionMap.put("useTicketCache", "true");
        optionMap.put("renewTGT", "true");
        optionMap.put("useKeyTab", "true");
        optionMap.put("storeKey", "true");
        optionMap.put("isInitiator", "true");
        optionMap.put("debug", "true");
        krb5LoginModule.initialize(subject, null, new HashMap<String,String>(), optionMap);
        boolean loginOk = krb5LoginModule.login();
        System.out.println("======= login:  " + loginOk);
        System.out.println("===========End login kerberos with java api===========");
    }

    public static UserGroupInformation loginWithHadoopApi(String krb5Path, String principal, String keytabPath) throws IOException {
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("sun.security.spnego.debug", "true");
        System.setProperty("java.security.krb5.conf", krb5Path);
        Configuration conf = new Configuration();
        conf.setBoolean("hadoop.security.authentication", true);
        conf.set("hadoop.security.authentication", "Kerberos");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabPath);
        System.out.println(UserGroupInformation.getLoginUser());
        UserGroupInformation ugiProxy = UserGroupInformation.createProxyUser("username", ugi);
        System.out.println(ugiProxy);
        System.out.println(ugi.isFromKeytab());
        System.out.println(ugiProxy.isFromKeytab());
        return ugi;
    }

    public static void httpClientKerberos(String krb5Path, String principal, String keytabPath) throws IOException {
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("sun.security.spnego.debug", "true");
        System.setProperty("java.security.krb5.conf", krb5Path);

        String url = "http://ip_addr:6080/service/plugins/secure/policies/download/trino?supportsPolicyDeltas=false&pluginId=trino@HOST-trino&clusterName=&lastActivationTime=1672132586347&pluginCapabilities=7ffff&lastKnownVersion=-1";
        Map<String, String> params = new HashMap<String, String>();
        params.put("lastKnownVersion", "-1");
        params.put("lastActivationTime", "1672132586347");
        params.put("pluginId", "trino@HOST-trino");
        params.put("clusterName", "");
        params.put("supportsPolicyDeltas", "false");
        params.put("pluginCapabilities", "7ffff");

        HttpClientBuilder builder = HttpClientBuilder.create();
        Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create().
                register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
        builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(null, -1, null), new Credentials() {
            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }
        });
        builder.setDefaultCredentialsProvider(credentialsProvider);
        CloseableHttpClient httpClient = builder.build();
        javax.security.auth.login.Configuration config = new javax.security.auth.login.Configuration() {
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, new HashMap<String, Object>() {
                    {
                        put("useTicketCache", "false");
                        put("useKeyTab", "true");
                        put("keyTab", keytabPath);
                        //Krb5 in GSS API needs to be refreshed so it does not throw the error
                        //Specified version of key is not available
                        put("refreshKrb5Config", "true");
                        put("principal", principal);
                        put("storeKey", "true");
                        put("doNotPrompt", "true");
                        put("isInitiator", "true");
                        put("debug", "true");
                    }
                })};
            }
        };
        Set<Principal> princ = new HashSet<Principal>(1);
        princ.add(new KerberosPrincipal("presto"));
        Subject sub = new Subject(false, princ, new HashSet<Object>(), new HashSet<Object>());
        try {
            //认证模块：Krb5Login
            LoginContext lc = new LoginContext("Krb5Login", sub, null, config);
            lc.login();
            Subject serviceSubject = lc.getSubject();
            Subject.doAs(serviceSubject, new PrivilegedAction<HttpResponse>() {
                HttpResponse httpResponse = null;

                @Override
                public HttpResponse run() {
                    try {
                        HttpUriRequest request = new HttpGet(url);
                        HttpClient spnegoHttpClient = httpClient;
                        httpResponse = spnegoHttpClient.execute(request);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        return null;
                    }
                    System.out.println(httpResponse.toString());
                    return httpResponse;
                }
            });
        } catch (Exception le) {
            le.printStackTrace();
        }

    }


    public static void jerseyClientKerberos(String krb5Path, String principal, String keytabPath) throws IOException {
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("sun.security.spnego.debug", "true");
        System.setProperty("java.security.krb5.conf", krb5Path);

        String url = "http://ip_addr:6080/service/plugins/secure/policies/download/trino?supportsPolicyDeltas=false&pluginId=trino@HOST-trino&clusterName=&lastActivationTime=1672132586347&pluginCapabilities=7ffff&lastKnownVersion=-1";
        Map<String, String> params = new HashMap<String, String>();
        params.put("lastKnownVersion", "-1");
        params.put("lastActivationTime", "1672132586347");
        params.put("pluginId", "trino@HOST-trino");
        params.put("clusterName", "");
        params.put("supportsPolicyDeltas", "false");
        params.put("pluginCapabilities", "7ffff");

        HttpClientBuilder builder = HttpClientBuilder.create();
        Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create().
                register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
        builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(null, -1, null), new Credentials() {
            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }
        });
        builder.setDefaultCredentialsProvider(credentialsProvider);
        CloseableHttpClient httpClient = builder.build();
        javax.security.auth.login.Configuration config = new javax.security.auth.login.Configuration() {
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, new HashMap<String, Object>() {
                    {
                        put("useTicketCache", "false");
                        put("useKeyTab", "true");
                        put("keyTab", keytabPath);
                        //Krb5 in GSS API needs to be refreshed so it does not throw the error
                        //Specified version of key is not available
                        put("refreshKrb5Config", "true");
                        put("principal", principal);
                        put("storeKey", "true");
                        put("doNotPrompt", "true");
                        put("isInitiator", "true");
                        put("debug", "true");
                    }
                })};
            }
        };
        Set<Principal> princ = new HashSet<Principal>(1);
        princ.add(new KerberosPrincipal("presto"));
        Subject sub = new Subject(false, princ, new HashSet<Object>(), new HashSet<Object>());
        try {
            //认证模块：Krb5Login
            LoginContext lc = new LoginContext("Krb5Login", sub, null, config);
            lc.login();
            Subject serviceSubject = lc.getSubject();
            Subject.doAs(serviceSubject, new PrivilegedAction<ClientResponse>() {
                HttpResponse httpResponse = null;
                ClientResponse clientRes = null;
                @Override
                public ClientResponse run() {

                    ClientConfig config = new DefaultClientConfig();
                    config.getClasses().add(JacksonJsonProvider.class);
                    Client client = Client.create(config);
                    WebResource webResource = client.resource(url);
                    webResource = setQueryParams(webResource, params);
                    clientRes = webResource.accept("application/json").type("application/json").get(ClientResponse.class);
                    System.out.println(clientRes.toString());
                    return clientRes;
                }
            });
        } catch (Exception le) {
            le.printStackTrace();
        }

        System.out.println("#############doAs Code:#############");
        UserGroupInformation user = loginWithHadoopApi(krb5Path, principal, keytabPath);
        user.doAs(new PrivilegedAction<ClientResponse>() {
            HttpResponse httpResponse = null;
            ClientResponse clientRes = null;
            @Override
            public ClientResponse run() {

                ClientConfig config = new DefaultClientConfig();
                config.getClasses().add(JacksonJsonProvider.class);
                Client client = Client.create(config);
                WebResource webResource = client.resource(url);
                webResource = setQueryParams(webResource, params);
                clientRes = webResource.accept("application/json").type("application/json").get(ClientResponse.class);
                System.out.println(clientRes.toString());
                return clientRes;
            }
        });




    }


    public static void rangerRestImpl(String krb5Path, String principal, String keytabPath) throws IOException {
        //Hadoop Api login and do action
        UserGroupInformation user = loginWithHadoopApi(krb5Path, principal, keytabPath);
        PrivilegedAction<ClientResponse> action = new PrivilegedAction<ClientResponse>() {
            public ClientResponse run() {
                ClientResponse clientRes = null;
                String url = "http://ip_addr:6080/service/plugins/secure/policies/download/trino?supportsPolicyDeltas=false&pluginId=trino@HOST-trino&clusterName=&lastActivationTime=1672132586347&pluginCapabilities=7ffff&lastKnownVersion=-1";
                Map<String, String> params = new HashMap<String, String>();
                params.put("lastKnownVersion", "-1");
                params.put("lastActivationTime", "1672132586347");
                params.put("pluginId", "trino@HOST-trino");
                params.put("clusterName", "");
                params.put("supportsPolicyDeltas", "false");
                params.put("pluginCapabilities", "7ffff");

                try {
                    // ranger实现
                    ClientConfig config = new DefaultClientConfig();
                    config.getClasses().add(JacksonJsonProvider.class);
                    Client client = Client.create(config);
                    WebResource webResource = client.resource(url);
                    webResource = setQueryParams(webResource, params);
                    clientRes = webResource.accept("application/json").type("application/json").get(ClientResponse.class);

                    // 测试
//                    HttpClientBuilder builder = HttpClientBuilder.create();
//                    Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create().
//                            register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
//                    builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
//                    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//                    credentialsProvider.setCredentials(new AuthScope(null, -1, null), new Credentials() {
//                        @Override
//                        public Principal getUserPrincipal() {
//                            return null;
//                        }
//
//                        @Override
//                        public String getPassword() {
//                            return null;
//                        }
//                    });
//                    builder.setDefaultCredentialsProvider(credentialsProvider);
//                    CloseableHttpClient httpClient = builder.build();
//                    javax.security.auth.login.Configuration config = new javax.security.auth.login.Configuration() {
//                        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
//                            return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
//                                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, new HashMap<String, Object>() {
//                                {
//                                    put("useTicketCache", "false");
//                                    put("useKeyTab", "true");
//                                    put("keyTab", keytabPath);
//                                    //Krb5 in GSS API needs to be refreshed so it does not throw the error
//                                    //Specified version of key is not available
//                                    put("refreshKrb5Config", "true");
//                                    put("principal", principal);
//                                    put("storeKey", "true");
//                                    put("doNotPrompt", "true");
//                                    put("isInitiator", "true");
//                                    put("debug", "true");
//                                }
//                            })};
//                        }
//                    };
//                    Set<Principal> princ = new HashSet<Principal>(1);
//                    princ.add(new KerberosPrincipal("presto"));
//                    Subject sub = new Subject(false, princ, new HashSet<Object>(), new HashSet<Object>());
//                    try {
//                        //认证模块：Krb5Login
//                        LoginContext lc = new LoginContext("Krb5Login", sub, null, config);
//                        lc.login();
//                        Subject serviceSubject = lc.getSubject();
//                        return (ClientResponse) Subject.doAs(serviceSubject, new PrivilegedAction<HttpResponse>() {
//                            HttpResponse httpResponse = null;
//                            @Override
//                            public HttpResponse run() {
//                                try {
//                                    HttpUriRequest request = new HttpGet(url);
//                                    HttpClient spnegoHttpClient = httpClient;
//                                    httpResponse = spnegoHttpClient.execute(request);
//                                    return httpResponse;
//                                } catch (IOException ioe) {
//                                    ioe.printStackTrace();
//                                }
//                                return httpResponse;
//                            }
//                        });
//                    } catch (Exception le) {
//                        le.printStackTrace();
//                    }
//
//

                } catch (Exception e) {
                    System.out.println("Failed to get response, Error is : " + e.getMessage());
                }
                return clientRes;
            }
        };
        ClientResponse clientResponse = user.doAs(action);
        ClientResponse run = action.run();
        System.out.println(run.toString());
        System.out.println("===============response after doAs===============");
        System.out.println(clientResponse.toString());

    }



}
