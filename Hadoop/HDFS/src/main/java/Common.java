import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class Common {
    private static transient DateTimeFormatter fmt;
    public static void setKerberos(Configuration conf, String krb5Path, String keytabPath, String keytabUser) throws IOException {
        System.setProperty("java.security.krb5.conf", krb5Path);
        conf.setBoolean("hadoop.security.authentication", true);
        conf.set("hadoop.security.authentication", "Kerberos");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(keytabUser, keytabPath);
    }

    public static String getHumanReadableTime(Long ts){
        fmt = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), TimeZone.getDefault().toZoneId());
        return dt.format(fmt);
    }
}
