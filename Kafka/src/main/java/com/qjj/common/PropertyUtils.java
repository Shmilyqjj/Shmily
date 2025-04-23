package com.qjj.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 参数处理
 * CreateTime: 2025/4/23 14:46
 * Author Shmily
 */
public class PropertyUtils {
    /**
     * 解析命令行参数
     */
    public static Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    params.put(key, args[++i]);
                } else {
                    params.put(key, "");
                }
            }
        }
        return params;
    }
}
