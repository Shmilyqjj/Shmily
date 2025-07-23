package common.utils;

/**
 * Description: oss工具类
 * CreateTime: 2025/7/23 21:07
 * Author Shmily
 */
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * OSS工具类
 */
public class OssUtils {
    private final OSS ossClient;
    private final String bucketName;

    /**
     * 构造函数
     *
     * @param endpoint     OSS endpoint (e.g. "<a href="https://oss-cn-hangzhou.aliyuncs.com">...</a>")
     * @param accessKeyId  AccessKey ID
     * @param accessKeySecret AccessKey Secret
     * @param bucketName   默认Bucket名称
     */
    public OssUtils(String endpoint, String accessKeyId, String accessKeySecret, String bucketName) {
        this.bucketName = bucketName;
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传文件到OSS
     *
     * @param objectName OSS对象名称 (包含路径)
     * @param file       要上传的文件
     * @return 文件URL
     */
    public String uploadFile(String objectName, File file) {
        try {
            ossClient.putObject(bucketName, objectName, file);
            return generateUrl(objectName);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("上传文件到OSS失败", e);
        }
    }

    /**
     * 上传字节数组到OSS
     *
     * @param objectName OSS对象名称 (包含路径)
     * @param bytes      字节数组
     * @return 文件URL
     */
    public String uploadBytes(String objectName, byte[] bytes) {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            ossClient.putObject(bucketName, objectName, inputStream);
            return generateUrl(objectName);
        } catch (OSSException | ClientException | IOException e) {
            throw new RuntimeException("上传字节数组到OSS失败", e);
        }
    }

    /**
     * 上传输入流到OSS
     *
     * @param objectName OSS对象名称 (包含路径)
     * @param inputStream 输入流
     * @return 文件URL
     */
    public String uploadStream(String objectName, InputStream inputStream) {
        try {
            ossClient.putObject(bucketName, objectName, inputStream);
            return generateUrl(objectName);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("上传输入流到OSS失败", e);
        }
    }

    /**
     * 下载文件为字节数组
     *
     * @param objectName OSS对象名称
     * @return 文件字节数组
     */
    public byte[] downloadBytes(String objectName) {
        try (OSSObject ossObject = ossClient.getObject(bucketName, objectName)) {
            return IOUtils.toByteArray(ossObject.getObjectContent());
        } catch (OSSException | ClientException | IOException e) {
            throw new RuntimeException("从OSS下载文件失败", e);
        }
    }

    /**
     * 下载文件到本地
     *
     * @param objectName OSS对象名称
     * @param localFile  本地文件路径
     */
    public void downloadFile(String objectName, File localFile) {
        try {
            ossClient.getObject(new GetObjectRequest(bucketName, objectName), localFile);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("从OSS下载文件失败", e);
        }
    }

    /**
     * 删除文件
     *
     * @param objectName OSS对象名称
     */
    public void deleteFile(String objectName) {
        try {
            ossClient.deleteObject(bucketName, objectName);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("删除OSS文件失败", e);
        }
    }

    /**
     * 批量删除文件
     *
     * @param objectNames OSS对象名称列表
     */
    public void deleteFiles(List<String> objectNames) {
        try {
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName)
                    .withKeys(objectNames);
            ossClient.deleteObjects(request);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("批量删除OSS文件失败", e);
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param objectName OSS对象名称
     * @return 是否存在
     */
    public boolean doesObjectExist(String objectName) {
        return ossClient.doesObjectExist(bucketName, objectName);
    }

    /**
     * 获取文件URL
     *
     * @param objectName OSS对象名称
     * @return 文件URL
     */
    public String generateUrl(String objectName) {
        // 设置URL过期时间为10年
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString().split("\\?")[0];
    }

    /**
     * 获取带签名的URL（临时访问）
     *
     * @param objectName OSS对象名称
     * @param expireTime 过期时间（毫秒）
     * @return 带签名的URL
     */
    public String generateSignedUrl(String objectName, long expireTime) {
        Date expiration = new Date(System.currentTimeMillis() + expireTime);
        URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString();
    }

    /**
     * 关闭OSSClient
     */
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    /**
     * 获取OSS客户端（用于需要直接操作OSSClient的场景）
     *
     * @return OSS客户端实例
     */
    public OSS getOssClient() {
        return ossClient;
    }
}