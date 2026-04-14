package plus.gaga.middleware.sdk.types.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用 API Token 生成工具（支持 DeepSeek 等模型）
 */
public class BearerTokenUtils {
    private static final Logger logger = LoggerFactory.getLogger(BearerTokenUtils.class);
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();
    private static final String API_KEY_PREFIX = "sk-";
    private static final int MAX_CACHE_SIZE = 1000;

    private static void validateKey(String k) {
        if (k == null || k.trim().isEmpty()) {
            throw new IllegalArgumentException("API Key 不能为空");
        }
    }

    /*
     * 获取认证 Token（默认 DeepSeek 格式）
     */
    public static String getToken(String apiKeySecret) {
        validateKey(apiKeySecret);

        if (!apiKeySecret.startsWith(API_KEY_PREFIX)) {
            logger.warn("API Key 不符合 DeepSeek 格式建议: {}...",
                    apiKeySecret.substring(0, Math.min(apiKeySecret.length(), 5)));
        }

        if (CACHE.size() >= MAX_CACHE_SIZE) {
            CACHE.clear();
            logger.warn("API Key 缓存已满，已自动清空");
        }

        return CACHE.computeIfAbsent(apiKeySecret, k -> k);
    }

    /*
     * 从环境变量获取 Token
     */
    public static String getTokenFromEnv(String envVarName) {
        if (envVarName == null || envVarName.trim().isEmpty()) {
            throw new IllegalArgumentException("环境变量名不能为空");
        }

        String apiKey = System.getenv(envVarName);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("未配置或为空的环境变量: " + envVarName);
        }
        return getToken(apiKey);
    }
}
