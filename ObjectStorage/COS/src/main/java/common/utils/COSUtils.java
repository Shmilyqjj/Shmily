package common.utils;

/**
 * Description: 腾讯云COS工具类
 * CreateTime: 2026/3/24
 * Author Shmily
 */

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import com.tencent.cloud.assumerole.AssumeRoleParam;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 腾讯云COS工具类
 */
public class COSUtils {
    private final COSClient cosClient;
    private final String bucketName;
    private final String region;
    private final String secretId;
    private final String secretKey;
    private String roleArn;
    private long durationSeconds = 1800L;
    private String basePath = "";

    /**
     * 构造函数
     *
     * @param region        地域 (e.g. "ap-guangzhou", "ap-shanghai")
     * @param secretId      腾讯云 SecretId
     * @param secretKey     腾讯云 SecretKey
     * @param bucketName    存储桶名称
     */
    public COSUtils(String region, String secretId, String secretKey, String bucketName) {
        this.bucketName = bucketName;
        this.region = region;
        this.secretId = secretId;
        this.secretKey = secretKey;

        // 初始化用户身份信息
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        // 设置bucket的地域
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 设置使用https协议
        clientConfig.setHttpProtocol(HttpProtocol.https);

        // 生成cos客户端
        this.cosClient = new COSClient(cred, clientConfig);
    }

    /**
     * 构造函数（带STS配置）
     *
     * @param region          地域
     * @param secretId        腾讯云 SecretId
     * @param secretKey       腾讯云 SecretKey
     * @param bucketName      存储桶名称
     * @param roleArn         角色ARN（用于AssumeRole获取临时凭证）
     * @param durationSeconds STS临时凭证有效期（秒）
     * @param basePath        文件基础路径前缀
     */
    public COSUtils(String region, String secretId, String secretKey, String bucketName,
                    String roleArn, long durationSeconds, String basePath) {
        this(region, secretId, secretKey, bucketName);
        this.roleArn = roleArn;
        this.durationSeconds = durationSeconds;
        this.basePath = basePath;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * 上传文件到COS
     *
     * @param objectName COS对象名称 (包含路径)
     * @param file       要上传的文件
     * @return 文件URL
     */
    public String uploadFile(String objectName, File file) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);
            cosClient.putObject(putObjectRequest);
            return generateUrl(objectName);
        } catch (CosClientException e) {
            throw new RuntimeException("上传文件到COS失败", e);
        }
    }

    /**
     * 上传字节数组到COS
     *
     * @param objectName COS对象名称 (包含路径)
     * @param bytes      字节数组
     * @return 文件URL
     */
    public String uploadBytes(String objectName, byte[] bytes) {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(bytes.length);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, objectMetadata);
            cosClient.putObject(putObjectRequest);
            return generateUrl(objectName);
        } catch (CosClientException | IOException e) {
            throw new RuntimeException("上传字节数组到COS失败", e);
        }
    }

    /**
     * 上传输入流到COS
     *
     * @param objectName  COS对象名称 (包含路径)
     * @param inputStream 输入流
     * @return 文件URL
     */
    public String uploadStream(String objectName, InputStream inputStream) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, new ObjectMetadata());
            cosClient.putObject(putObjectRequest);
            return generateUrl(objectName);
        } catch (CosClientException e) {
            throw new RuntimeException("上传输入流到COS失败", e);
        }
    }

    /**
     * 上传输入流到COS（带元数据）
     *
     * @param objectName     COS对象名称 (包含路径)
     * @param inputStream    输入流
     * @param contentLength  内容长度
     * @return 文件URL
     */
    public String uploadStream(String objectName, InputStream inputStream, long contentLength) {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contentLength);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, objectMetadata);
            cosClient.putObject(putObjectRequest);
            return generateUrl(objectName);
        } catch (CosClientException e) {
            throw new RuntimeException("上传输入流到COS失败", e);
        }
    }

    /**
     * 下载文件为字节数组
     *
     * @param objectName COS对象名称
     * @return 文件字节数组
     */
    public byte[] downloadBytes(String objectName) {
        try (COSObject cosObject = cosClient.getObject(bucketName, objectName)) {
            return IOUtils.toByteArray(cosObject.getObjectContent());
        } catch (CosClientException | IOException e) {
            throw new RuntimeException("从COS下载文件失败", e);
        }
    }

    /**
     * 下载文件到本地
     *
     * @param objectName COS对象名称
     * @param localFile  本地文件路径
     */
    public void downloadFile(String objectName, File localFile) {
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
            cosClient.getObject(getObjectRequest, localFile);
        } catch (CosClientException e) {
            throw new RuntimeException("从COS下载文件失败", e);
        }
    }

    /**
     * 获取文件输入流
     *
     * @param objectName COS对象名称
     * @return 文件输入流
     */
    public InputStream downloadStream(String objectName) {
        try {
            COSObject cosObject = cosClient.getObject(bucketName, objectName);
            return cosObject.getObjectContent();
        } catch (CosClientException e) {
            throw new RuntimeException("从COS获取文件流失败", e);
        }
    }

    /**
     * 删除文件
     *
     * @param objectName COS对象名称
     */
    public void deleteFile(String objectName) {
        try {
            cosClient.deleteObject(bucketName, objectName);
        } catch (CosClientException e) {
            throw new RuntimeException("删除COS文件失败", e);
        }
    }

    /**
     * 批量删除文件
     *
     * @param objectNames COS对象名称列表
     */
    public void deleteFiles(List<String> objectNames) {
        try {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
            // 设置要删除的key列表, 最多一次删除1000个
            List<DeleteObjectsRequest.KeyVersion> keys = new java.util.ArrayList<>();
            for (String key : objectNames) {
                keys.add(new DeleteObjectsRequest.KeyVersion(key));
            }
            deleteObjectsRequest.setKeys(keys);
            cosClient.deleteObjects(deleteObjectsRequest);
        } catch (CosClientException e) {
            throw new RuntimeException("批量删除COS文件失败", e);
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param objectName COS对象名称
     * @return 是否存在
     */
    public boolean doesObjectExist(String objectName) {
        try {
            return cosClient.doesObjectExist(bucketName, objectName);
        } catch (CosClientException e) {
            throw new RuntimeException("检查COS文件是否存在失败", e);
        }
    }

    /**
     * 获取文件元数据
     *
     * @param objectName COS对象名称
     * @return 文件元数据
     */
    public ObjectMetadata getObjectMetadata(String objectName) {
        try {
            return cosClient.getObjectMetadata(bucketName, objectName);
        } catch (CosClientException e) {
            throw new RuntimeException("获取COS文件元数据失败", e);
        }
    }

    /**
     * 获取文件URL
     *
     * @param objectName COS对象名称
     * @return 文件URL
     */
    public String generateUrl(String objectName) {
        // 设置URL过期时间为10年
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        URL url = cosClient.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString().split("\\?")[0];
    }

    /**
     * 获取带签名的URL（临时访问）
     *
     * @param objectName COS对象名称
     * @param expireTime 过期时间（毫秒）
     * @return 带签名的URL
     */
    public String generateSignedUrl(String objectName, long expireTime) {
        Date expiration = new Date(System.currentTimeMillis() + expireTime);
        URL url = cosClient.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString();
    }

    /**
     * 列举存储桶中的对象
     *
     * @param prefix 对象前缀
     * @return 对象列表
     */
    public List<COSObjectSummary> listObjects(String prefix) {
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setBucketName(bucketName);
            listObjectsRequest.setPrefix(prefix);
            listObjectsRequest.setMaxKeys(1000);
            ObjectListing objectListing = cosClient.listObjects(listObjectsRequest);
            return objectListing.getObjectSummaries();
        } catch (CosClientException e) {
            throw new RuntimeException("列举COS对象失败", e);
        }
    }

    /**
     * 复制对象
     *
     * @param srcObjectName  源对象名称
     * @param destObjectName 目标对象名称
     */
    public void copyObject(String srcObjectName, String destObjectName) {
        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, srcObjectName, bucketName, destObjectName);
            cosClient.copyObject(copyObjectRequest);
        } catch (CosClientException e) {
            throw new RuntimeException("复制COS对象失败", e);
        }
    }

    /**
     * 关闭COSClient
     */
    public void shutdown() {
        if (cosClient != null) {
            cosClient.shutdown();
        }
    }

    /**
     * 获取COS客户端（用于需要直接操作COSClient的场景）
     *
     * @return COS客户端实例
     */
    public COSClient getCosClient() {
        return cosClient;
    }

    /**
     * 获取存储桶名称
     *
     * @return 存储桶名称
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 获取地域
     *
     * @return 地域
     */
    public String getRegion() {
        return region;
    }

    /**
     * 上传字符串内容到COS并返回STS预签名下载URL
     * 使用长期凭证上传文件，使用STS临时凭证生成预签名URL（更安全）
     *
     * @param content  文件内容
     * @param fileName 文件名
     * @return STS预签名下载URL
     */
    public String uploadWithStsUrl(String content, String fileName) {
        String objectName = basePath.isEmpty() ? fileName : basePath + "/" + fileName;
        return uploadWithStsUrl(objectName, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 上传字节数组到COS并返回STS预签名下载URL
     * 使用长期凭证上传文件，使用STS临时凭证生成预签名URL（更安全）
     *
     * @param objectName COS对象名称（包含路径）
     * @param bytes      字节数组
     * @return STS预签名下载URL
     */
    public String uploadWithStsUrl(String objectName, byte[] bytes) {
        try {
            // 1. 使用长期凭证上传文件
            try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(bytes.length);
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, objectMetadata);
                cosClient.putObject(putObjectRequest);
            }

            // 2. 使用STS临时凭证生成预签名下载URL
            return generateStsPresignedUrl(objectName);
        } catch (CosClientException | IOException e) {
            throw new RuntimeException("上传文件到COS并生成STS预签名URL失败", e);
        }
    }

    /**
     * 使用STS临时凭证生成预签名下载URL
     *
     * @param objectName COS对象名称
     * @return 预签名URL
     */
    public String generateStsPresignedUrl(String objectName) {
        return generateStsPresignedUrl(objectName, 24 * 60 * 60 * 1000); // 默认24小时有效
    }

    /**
     * 使用STS临时凭证生成预签名下载URL
     *
     * @param objectName     COS对象名称
     * @param expireTimeMillis URL过期时间（毫秒）
     * @return 预签名URL
     */
    public String generateStsPresignedUrl(String objectName, long expireTimeMillis) {
        try {
            // 1. 获取STS临时凭证
            Response stsResponse = getStsCredentials();
            String tmpSecretId = stsResponse.credentials.tmpSecretId;
            String tmpSecretKey = stsResponse.credentials.tmpSecretKey;
            String sessionToken = stsResponse.credentials.sessionToken;

            // 2. 使用临时凭证创建COS客户端
            COSCredentials cred = new BasicSessionCredentials(tmpSecretId, tmpSecretKey, sessionToken);
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            COSClient stsClient = new COSClient(cred, clientConfig);

            try {
                // 3. 生成预签名URL
                Date expirationDate = new Date(System.currentTimeMillis() + expireTimeMillis);
                GeneratePresignedUrlRequest req =
                        new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethodName.GET);
                req.setExpiration(expirationDate);

                URL url = stsClient.generatePresignedUrl(req);
                return url.toString();
            } finally {
                stsClient.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException("生成STS预签名URL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取STS临时凭证
     * 如果配置了roleArn，使用AssumeRole接口；否则使用GetFederationToken接口
     */
    private Response getStsCredentials() throws Exception {
        if (roleArn != null && !roleArn.isEmpty()) {
            return getStsCredentialsByAssumeRole();
        } else {
            return getStsCredentialsByFederationToken();
        }
    }

    /**
     * 使用AssumeRole接口获取临时凭证
     */
    private Response getStsCredentialsByAssumeRole() throws Exception {
        AssumeRoleParam assumeRoleParam = new AssumeRoleParam();
        assumeRoleParam.setSecretId(secretId);
        assumeRoleParam.setSecretKey(secretKey);
        assumeRoleParam.setRegion(region);
        assumeRoleParam.setRoleArn(roleArn);
        assumeRoleParam.setRoleSessionName("cos-session");
        assumeRoleParam.setHost("sts.tencentcloudapi.com");
        assumeRoleParam.setSignatureMethod("HmacSHA256");
        assumeRoleParam.setDurationSec((int) durationSeconds);
        assumeRoleParam.setPolicy(buildDefaultPolicy());

        Response response = CosStsClient.getRoleCredential(assumeRoleParam);
        // AssumeRole返回的Token在token字段，需要转到sessionToken字段
        if (response.credentials.token != null) {
            response.credentials.sessionToken = response.credentials.token;
        }
        return response;
    }

    /**
     * 使用GetFederationToken接口获取临时凭证
     */
    private Response getStsCredentialsByFederationToken() throws Exception {
        TreeMap<String, Object> config = new TreeMap<>();
        config.put("secretId", secretId);
        config.put("secretKey", secretKey);
        config.put("durationSeconds", durationSeconds);
        config.put("domain", "sts.tencentcloudapi.com");
        config.put("region", region);
        config.put("policy", buildDefaultPolicy());

        return CosStsClient.getCredential(config);
    }

    /**
     * 构建默认的STS策略
     * 限制临时密钥只能对指定bucket执行getObject操作
     */
    private String buildDefaultPolicy() {
        String resourcePath = basePath.isEmpty() ? "*" : basePath + "/*";
        String bucket = "qcs::cos:" + region + ":uid/" + getAppIdFromBucket() + ":" + bucketName + "/" + resourcePath;

        Map<String, Object> policyMap = new HashMap<>();
        policyMap.put("version", "2.0");

        Map<String, Object> statement = new HashMap<>();
        statement.put("effect", "allow");
        statement.put("action", new String[]{"name/cos:GetObject"});
        statement.put("resource", new String[]{bucket});

        policyMap.put("statement", new Map[]{statement});

        return toJsonString(policyMap);
    }

    /**
     * 从bucket名称中提取appId
     * COS bucket格式通常为: bucketName-appId
     */
    private String getAppIdFromBucket() {
        if (bucketName == null || bucketName.isEmpty()) {
            return "";
        }
        int lastDashIndex = bucketName.lastIndexOf('-');
        if (lastDashIndex > 0 && lastDashIndex < bucketName.length() - 1) {
            return bucketName.substring(lastDashIndex + 1);
        }
        return "";
    }

    /**
     * 简单的JSON序列化方法
     */
    private String toJsonString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value instanceof String[]) {
                sb.append("[");
                String[] arr = (String[]) value;
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) sb.append(",");
                    sb.append("\"").append(arr[i]).append("\"");
                }
                sb.append("]");
            } else if (value instanceof Map[]) {
                sb.append("[");
                Map[] arr = (Map[]) value;
                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) sb.append(",");
                    sb.append(toJsonString(arr[i]));
                }
                sb.append("]");
            } else {
                sb.append(value);
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
