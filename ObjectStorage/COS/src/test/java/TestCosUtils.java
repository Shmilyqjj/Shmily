import common.utils.COSUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Description: COS工具类测试
 * CreateTime: 2026/3/24
 * Author Shmily
 */
public class TestCosUtils {
    private static final String localFilePath = "/home/shmily/Downloads/2107887671-2118595678.mp4";
    private static final String regionId = "ap-shanghai";
    private static final String AK = "xxxx";
    private static final String SK = "xxxx";
    private static final String BucketName = "xxxx";
    private static final String roleArn = "qcs::cam::uin/xxxx:roleName/xxx-cos-role";

    public static void main(String[] args) {
        testBasicOperations();
        System.out.println("\n========== STS预签名URL测试 ==========\n");
        testStsPresignedUrl();
        testUploadAndGetUrl();
    }

    /**
     * 基础操作测试
     */
    private static void testBasicOperations() {
        // 创建COS工具类实例
        // region: 地域，如 ap-guangzhou(广州), ap-shanghai(上海), ap-beijing(北京)
        // secretId/secretKey: 在腾讯云控制台 - 访问管理 - 访问密钥 中获取
        // bucketName: 存储桶名称，格式为 bucketname-appid
        COSUtils cosUtils = new COSUtils(
                regionId,
                AK,
                SK,
                BucketName
        );

        try {
            // 上传文件示例
            String targetObject = "test/2107887671-2118595678.mp4";
            File file = new File(localFilePath);
            String url = cosUtils.uploadFile(targetObject, file);
            System.out.println("文件上传成功，URL: " + url);

            // 检查文件是否存在
            boolean exists = cosUtils.doesObjectExist(targetObject);
            System.out.println("文件是否存在: " + exists);

            // 获取文件元数据
            com.qcloud.cos.model.ObjectMetadata metadata = cosUtils.getObjectMetadata(targetObject);
            System.out.println("文件大小: " + metadata.getContentLength());
            System.out.println("最后修改时间: " + metadata.getLastModified());

            // 下载文件示例
            byte[] content = cosUtils.downloadBytes(targetObject);
            System.out.println("下载文件内容长度: " + content.length);

            // 列举对象示例
            System.out.println("列举对象:");
            cosUtils.listObjects("test/").forEach(obj -> 
                System.out.println("  - " + obj.getKey() + " (" + obj.getSize() + " bytes)")
            );

            // 复制对象示例
            String copyTarget = "test/tb_location_copy.tsv";
            cosUtils.copyObject(targetObject, copyTarget);
            System.out.println("文件复制成功");

            // 生成临时访问URL
            String signedUrl = cosUtils.generateSignedUrl(targetObject, 3600 * 1000); // 1小时有效
            System.out.println("临时访问URL: " + signedUrl);

            // 批量删除示例
            cosUtils.deleteFiles(Arrays.asList(targetObject, copyTarget));
            System.out.println("文件已删除");

        } finally {
            // 关闭COS客户端
            cosUtils.shutdown();
        }
    }

    /**
     * STS预签名URL测试
     * 使用STS临时凭证生成预签名下载URL，更安全
     */
    private static void testStsPresignedUrl() {
        // 创建COS工具类实例（带STS配置）
        // roleArn: 角色ARN，格式为 qcs::cam::uin/ownerUin:roleName/roleName
        // durationSeconds: STS临时凭证有效期（秒）
        // basePath: 文件基础路径前缀
        COSUtils cosUtils = new COSUtils(
                regionId,
                AK,
                SK,
                BucketName,
                roleArn,           // roleArn，为null时使用GetFederationToken方式
                1800L,          // 临时凭证有效期30分钟
                "test"      // 文件基础路径
        );

        try {
            // 1. 上传字符串内容并获取STS预签名下载URL
            String content = "这是一个测试文件，用于验证STS预签名URL功能";
            String fileName = "test-sts-" + System.currentTimeMillis() + ".txt";
            String stsUrl = cosUtils.uploadWithStsUrl(content, fileName);
            System.out.println("上传成功，STS预签名下载URL:");
            System.out.println(stsUrl);

            // 2. 对已存在的文件生成STS预签名下载URL
            String existingFile = "test/existing-file.txt";
            if (cosUtils.doesObjectExist(existingFile)) {
                String presignedUrl = cosUtils.generateStsPresignedUrl(existingFile);
                System.out.println("\n已存在文件的STS预签名URL:");
                System.out.println(presignedUrl);

                // 3. 生成自定义过期时间的STS预签名URL（1小时有效）
                String customUrl = cosUtils.generateStsPresignedUrl(existingFile, 60 * 60 * 1000L);
                System.out.println("\n自定义过期时间（1小时）的STS预签名URL:");
                System.out.println(customUrl);
            } else {
                System.out.println("\n测试文件不存在，跳过已存在文件的STS URL生成测试");
            }

            // 4. 上传字节数组并获取STS预签名URL
            byte[] bytes = "测试字节数组上传".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String byteFileName = "test-bytes-" + System.currentTimeMillis() + ".bin";
            String byteStsUrl = cosUtils.uploadWithStsUrl(byteFileName, bytes);
            System.out.println("\n字节数组上传成功，STS预签名URL:");
            System.out.println(byteStsUrl);

        } catch (Exception e) {
            System.err.println("STS预签名URL测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cosUtils.shutdown();
        }


    }

    private static void testUploadAndGetUrl() {
        COSUtils cosUtils = new COSUtils(
                regionId,
                AK,
                SK,
                BucketName,
                roleArn,           // roleArn，为null时使用GetFederationToken方式
                1800L,          // 临时凭证有效期30分钟
                "test"      // 文件基础路径
        );
        String url = cosUtils.uploadWithStsUrl("test_content", "test/test.csv");
        System.out.println("文件下载链接" + url);
    }
}
