package common.utils;

/**
 * Description: 腾讯云COS工具类
 * CreateTime: 2026/3/24
 * Author Shmily
 */

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * 腾讯云COS工具类
 */
public class COSUtils {
    private final COSClient cosClient;
    private final String bucketName;
    private final String region;

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
}
