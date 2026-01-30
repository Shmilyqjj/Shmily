package String;

import common.MyAnnotation;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MyAnnotation
public class CompareVersions {
    private static final Pattern VERSION_PATTERN = Pattern.compile("^(?:[vV])?([0-9]+(?:\\.[0-9]+)*)");
    private static final Pattern BUILD_METADATA_PATTERN = Pattern.compile("\\+.*$");
    private static final Pattern PRE_RELEASE_PATTERN = Pattern.compile("-.*$");


    @MyAnnotation
    public static void main(String[] args) throws IOException {
        int evaluate = evaluate("v0.0.3", "0.0.2");
        System.out.println(evaluate);
    }

    public static int evaluate(String version1,String version2) throws IOException {
        if (version1 == null || version2 == null) {
            if (Objects.equals(version1, version2)) {
                return 0;
            }
            return 2;
        }

        String v1 = version1.toString().trim();
        String v2 = version2.toString().trim();

        // 处理空字符串
        if (v1.isEmpty() && v2.isEmpty()) {
            return 0;
        }
        if (v1.isEmpty()) {
            return -1;
        }
        if (v2.isEmpty()) {
            return 1;
        }

        // 标准化版本号
        String normalizedV1 = normalizeVersion(v1);
        String normalizedV2 = normalizeVersion(v2);

        // 分割版本号部分
        String[] v1Parts = normalizedV1.split("\\.");
        String[] v2Parts = normalizedV2.split("\\.");

        int maxLength = Math.max(v1Parts.length, v2Parts.length);

        for (int i = 0; i < maxLength; i++) {
            int v1Part = (i < v1Parts.length) ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = (i < v2Parts.length) ? Integer.parseInt(v2Parts[i]) : 0;

            if (v1Part > v2Part) {
                return 1;
            } else if (v1Part < v2Part) {
                return -1;
            }
        }

        return 0;
    }

    /**
     * 标准化版本号字符串
     * 1. 去除前缀 'v' 或 'V'
     * 2. 去除构建元数据 (+后面的部分)
     * 3. 去除预发布标识符 (-后面的部分)
     * 4. 提取数字版本部分
     */
    private static String normalizeVersion(String version) {
        // 去除构建元数据
        String cleaned = BUILD_METADATA_PATTERN.matcher(version).replaceFirst("");
        // 去除预发布标识符
        cleaned = PRE_RELEASE_PATTERN.matcher(cleaned).replaceFirst("");

        // 提取版本号数字部分
        Matcher matcher = VERSION_PATTERN.matcher(cleaned);
        if (matcher.find()) {
            return matcher.group(1);
        }

        // 如果没有找到数字版本，返回原始字符串（去除特殊字符后）
        return cleaned.replaceAll("[^0-9.]", "");
    }

}

