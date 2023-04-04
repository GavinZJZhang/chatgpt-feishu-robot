package group.delonix.chatgpt4.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

/**
 * @author
 */
public class FeishuUtils {
    static Logger log = LoggerFactory.getLogger(FeishuUtils.class);
    public static final String tokenUrl
            = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal/";
    /**
     * 构建一个cache 缓存飞书的token
     */
    static Cache<String, String> tokenCache =
            CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(3500)).build();
    /**
     * 这个是飞书应用的appId和key，可以在创建的飞书应用中找到
     */
    public static final String appId = "";
    public static final String appKey = "";

    /**
     * anybot-
     * @return
     */
//    public static final String appId = "cli_a4a2b9141538500c";
//    public static final String appKey = "rdQDvfUNXBwEtNQCBgYFkbwxFpovFokw";

    public static String getTenantAccessToken() {
        String token = null;
        try {
            token = tokenCache.get("token", () -> {
                JSONObject params = new JSONObject();
                params.put("app_id", appId);
                params.put("app_secret", appKey);
                String body;
                try (HttpResponse execute = HttpUtil.createPost(tokenUrl)
                        .body(params.toJSONString()).execute()) {
                    body = execute.body();
                }
                log.info("获取飞书token:{}", body);
                if (StrUtil.isNotBlank(body)) {
                    String tenantAccessToken = JSONObject.parseObject(body).getString("tenant_access_token");
                    tokenCache.put("token", tenantAccessToken);
                    return tenantAccessToken;
                }
                return null;
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    public static boolean checkFromFeishu(String body) {
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject header = jsonObject.getJSONObject("header");
        return appId.equals(header.getString("app_id"));
    }

    public static void checkFromFeishuThrows(String body) {
        if (!checkFromFeishu(body)) {
            log.error("此消息非飞书发送！ 消息报文:{}", body);
            throw new RuntimeException("此消息非飞书发送！");
        }
    }

}
