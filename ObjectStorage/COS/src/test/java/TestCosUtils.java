import common.utils.COSUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Description: COS工具类测试
 * CreateTime: 2026/3/24
 * Author Shmily
 */
public class TestCosUtils {
    public static void main(String[] args) {
        // 创建COS工具类实例
        // region: 地域，如 ap-guangzhou(广州), ap-shanghai(上海), ap-beijing(北京)
        // secretId/secretKey: 在腾讯云控制台 - 访问管理 - 访问密钥 中获取
        // bucketName: 存储桶名称，格式为 bucketname-appid
        COSUtils cosUtils = new COSUtils(
                "ap-shanghai",
                "xxx",
                "xxx",
                "bucket_name"
        );

        try {
            // 上传文件示例
            String localFilePath = "/home/shmily/Downloads/2107887671-2118595678.mp4";
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
}
