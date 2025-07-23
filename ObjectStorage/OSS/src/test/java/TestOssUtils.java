import common.utils.OssUtils;

import java.io.File;

/**
 * Description: OssUtils测试
 * CreateTime: 2025/7/23 21:10
 * Author Shmily
 */
public class TestOssUtils {
    public static void main(String[] args) {
        OssUtils ossUtils = new OssUtils(
                "https://oss-cn-shenzhen.aliyuncs.com",
                "your-access-key",
                "your-secret-key",
                "bucket-name"
        );

        try {
            // 上传文件示例
            String localFilePath = "/home/shmily/Downloads/tb_location.tsv";
            String targetObject = "test/tb_location.tsv";
            File file = new File(localFilePath);
            String url = ossUtils.uploadFile(targetObject, file);
            System.out.println("文件上传成功，URL: " + url);

            // 检查文件是否存在
            boolean exists = ossUtils.doesObjectExist(targetObject);
            System.out.println("文件是否存在: " + exists);

            // 下载文件示例
            byte[] content = ossUtils.downloadBytes(targetObject);
            System.out.println("下载文件内容长度: " + content.length);

            // 生成临时访问URL
            String signedUrl = ossUtils.generateSignedUrl(targetObject, 3600 * 1000); // 1小时有效
            System.out.println("临时访问URL: " + signedUrl);

            // 删除文件
            ossUtils.deleteFile(targetObject);
            System.out.println("文件已删除");
        } finally {
            // 关闭OSS客户端
            ossUtils.shutdown();
        }
    }
}
