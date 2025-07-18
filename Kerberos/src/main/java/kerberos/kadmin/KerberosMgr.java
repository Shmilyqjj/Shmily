package kerberos.kadmin;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class KerberosMgr {
    public static final Logger logger = LoggerFactory.getLogger(KerberosMgr.class);
    private String kdcServer;
    private String adminPrincipal;
    private String adminKeytabPath;
    private String realm;
    private String kAdminExecutable = "/usr/bin/kadmin";
    private String kadminHideStr;

    public KerberosMgr(String kdcServer, String adminPrincipal, String adminKeytabPath, String realm) {
        this.kdcServer = kdcServer;
        this.adminPrincipal = adminPrincipal;
        this.adminKeytabPath = adminKeytabPath;
        this.realm = realm;
        this.kadminHideStr = String.format("Authenticating as principal %s with keytab %s.\n", adminPrincipal, adminKeytabPath);
    }

    public void setKAdminExecutable(String path) {
        this.kAdminExecutable = path;
    }

    /**
     * 执行kadmin命令
     * @param command kadmin命令
     * @return 命令执行结果
     * @throws IOException
     * @throws InterruptedException
     */
    private String executeKAdminCommand(String command) throws IOException, InterruptedException {
        List<String> commandList = new ArrayList<>();
        commandList.add(kAdminExecutable);
        commandList.add("-s");
        commandList.add(kdcServer);
        commandList.add("-kt");
        commandList.add(adminKeytabPath);
        commandList.add("-p");
        commandList.add(adminPrincipal);
        commandList.add("-q");
        commandList.add(command);
        ProcessBuilder pb = new ProcessBuilder(commandList);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("kadmin command failed with exit code " + exitCode + "\n" + output);
        }
        return output.toString().replace(kadminHideStr, "");
    }

    /**
     * 创建新的Kerberos principal
     * @param principal principal名称
     * @param password principal密码
     * @return 是否创建成功
     */
    public String createPrincipal(String principal, String password) throws IOException, InterruptedException {
        String fullPrincipal = principal.contains(realm) ? principal : principal + "@" + realm;
        logger.info("create principal {}", fullPrincipal);
        if (password != null) {
            return executeKAdminCommand("addprinc -pw " + password + " " + fullPrincipal);
        } else {
            return executeKAdminCommand("addprinc -randkey " + fullPrincipal);
        }
    }


    /**
     * 生成keytab文件
     * @param principal principal名称（不含@REALM）
     * @param outputKeytabPath 输出的keytab文件路径
     * @return 执行结果
     */
    public String generateKeytabFile(String principal, String outputKeytabPath) throws IOException, InterruptedException {
        String fullPrincipal = principal.contains(realm) ? principal : principal + "@" + realm;
        logger.info("generate keytab file {} for principal {}", outputKeytabPath, fullPrincipal);
        File keytabFile = new File(outputKeytabPath);
        if (keytabFile.exists()) {
            keytabFile.delete();
        }
        return executeKAdminCommand("ktadd -k " + outputKeytabPath + " " + fullPrincipal);
    }

    /**
     * 删除keytab文件
     * @param keytabPath keytab文件路径
     * @return 是否删除成功
     */
    public boolean deleteKeytabFile(String keytabPath) {
        logger.info("delete keytab file {}", keytabPath);
        if (StringUtils.isBlank(keytabPath)) {
            throw new IllegalArgumentException("Keytab path cannot be empty");
        }

        File keytabFile = new File(keytabPath);
        return keytabFile.exists() && keytabFile.delete();
    }

    /**
     * 重新生成keytab文件
     * @param principal principal名称
     * @param oldKeytabPath 旧keytab文件路径 nullable
     * @param newKeytabPath 新keytab文件路径
     * @return 是否重新生成成功
     */
    public String regenerateKeytabFile(String principal, String oldKeytabPath, String newKeytabPath) throws IOException, InterruptedException {
        logger.info("regenerate keytab file, old: {} new: {}", oldKeytabPath, newKeytabPath);
        String fullPrincipal = principal.contains(realm) ? principal : principal + "@" + realm;
        if (StringUtils.isBlank(fullPrincipal) || StringUtils.isBlank(newKeytabPath)) {
            throw new IllegalArgumentException("Principal name and new keytab path cannot be empty");
        }
        File oldKeytab = new File(oldKeytabPath);
        if (oldKeytab.exists()) {
            oldKeytab.delete();
        }
        return generateKeytabFile(fullPrincipal, newKeytabPath);
    }

    /**
     * 修改principal密码
     * @param principal principal名称（不含@REALM）
     * @param newPassword 新密码
     * @return 执行结果
     */
    public String changePrincipalPassword(String principal, String newPassword) throws IOException, InterruptedException {
        String fullPrincipal = principal.contains(realm) ? principal : principal + "@" + realm;
        logger.info("change password for principal: {}", fullPrincipal);
        return executeKAdminCommand("cpw -pw " + newPassword + " " + fullPrincipal);
    }

    /**
     * 删除principal
     * @param principal principal名称
     * @return 执行结果
     */
    public String deletePrincipal(String principal) throws IOException, InterruptedException {
        String fullPrincipal = principal.contains(realm) ? principal : principal + "@" + realm;
        logger.info("delete principal {}", fullPrincipal);
        return executeKAdminCommand("delprinc -force " + fullPrincipal);
    }

    /**
     * 查询principal是否存在
     * @param principal principal名称
     * @return principal是否存在
     */
    public boolean principalExists(String principal) throws IOException, InterruptedException {
        if (StringUtils.isBlank(principal)) {
            throw new IllegalArgumentException("Principal name cannot be empty");
        }
        String fullPrincipal = principal.contains(realm) ? principal : principal + "@" + realm;
        String out = executeKAdminCommand("listprincs " + fullPrincipal);
        Pattern pattern = Pattern.compile(String.format("^%s\n", fullPrincipal));
        return pattern.matcher(out).find();
    }

    /**
     * 匹配获取principal
     * @param pattern *表示所有
     * @return principal列表
     */
    public String listPrincipals(String pattern) throws IOException, InterruptedException {
        return executeKAdminCommand("listprincs " + pattern);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        KerberosMgr kerberosMgr = new KerberosMgr(
                "127.0.0.1:750",
                "admin/admin@SHMILY-QJJ.TOP",
                "/opt/modules/keytabs/admin.keytab",
                "SHMILY-QJJ.TOP"
        );

        System.out.println(kerberosMgr.executeKAdminCommand("listprincs shmily@SHMILY-QJJ.TOP") + "\n---------------\n");
        System.out.println(kerberosMgr.createPrincipal("test", "123456") + "\n---------------\n");
        System.out.println(kerberosMgr.listPrincipals("test*") + "\n---------------\n");
        System.out.println(kerberosMgr.principalExists("test") + "\n---------------\n");
        System.out.println(kerberosMgr.generateKeytabFile("test", "/tmp/test.keytab") + "\n---------------\n");
        System.out.println(kerberosMgr.changePrincipalPassword("test", "1234567") + "\n---------------\n");
        System.out.println(kerberosMgr.regenerateKeytabFile("test", "/tmp/test.keytab","/tmp/test.keytab") + "\n---------------\n");
        System.out.println(kerberosMgr.deleteKeytabFile("/tmp/test.keytab") + "\n---------------\n");
        System.out.println(kerberosMgr.deletePrincipal("test") + "\n---------------\n");
    }
}