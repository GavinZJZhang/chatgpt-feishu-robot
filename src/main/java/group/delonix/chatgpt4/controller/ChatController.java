package group.delonix.chatgpt4.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import group.delonix.chatgpt4.controller.dto.FeishuEventDTO;
import group.delonix.chatgpt4.controller.dto.FeishuEventParams;
import group.delonix.chatgpt4.dao.vo.FeishuResponse;
import group.delonix.chatgpt4.entity.ChatContext;
import group.delonix.chatgpt4.utils.ChatContextUtils;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import group.delonix.chatgpt4.utils.FeishuUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zzj
 */
@RestController
public class ChatController {
    Logger log = LoggerFactory.getLogger(group.delonix.chatgpt4.controller.ChatController.class);

    public static ConcurrentLinkedQueue<FeishuResponse> consumer
            = new ConcurrentLinkedQueue<>();
    public static ConcurrentHashMap<String, ArrayList<ChatContext>> context
            = new ConcurrentHashMap<>();

    @PostMapping({"/delonix/chat"})
    public String delonixChat(@RequestBody String body) {
        this.log.info("收到飞书消息:{}", body);
        String userId = addQueryToConsumer(body);
//        limitContext(userId); //TODO:limitContext放在这里是不对的

        return "suc";
    }

    private FeishuEventDTO feishuChallenge(String body) {
        FeishuEventParams feishuEventParams = (FeishuEventParams)JSONObject.parseObject(body, FeishuEventParams.class);
        FeishuEventDTO eventDTO = new FeishuEventDTO();
        eventDTO.setChallenge(feishuEventParams.getChallenge());
        return eventDTO;
    }

    private String addQueryToConsumer(String body) {
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject header = jsonObject.getJSONObject("header");
        String eventType = header.getString("event_type");
        if ("im.message.receive_v1".equals(eventType)) {
            JSONObject event = jsonObject.getJSONObject("event");
            JSONObject message = event.getJSONObject("message");
            JSONObject sender = event.getJSONObject("sender");
            String messageType = message.getString("message_type");
            String senderType = sender.getString("sender_type");
            if ("text".equals(messageType) && "user".equals(senderType)) {
                String messageId = message.getString("message_id");
                String content = message.getString("content");
                JSONObject contentJson = JSONObject.parseObject(content);
                String text = contentJson.getString("text");
                JSONObject senderId = sender.getJSONObject("sender_id");
                String userId = senderId.getString("user_id");
                if (text.contains("@_all")) {
                    log.info("此消息是@所有人的，现在机器人不会回复@所有人");
                    return "";
                }
                if (text.startsWith("@_user_")) {
                    log.info("此消息头是@_user_, 已处理");
                    text = text.substring(text.indexOf(" ")+1, text.length());
                }


                FeishuResponse feishuResponse = new FeishuResponse();
                feishuResponse.setMessageId(messageId);
                feishuResponse.setQuery(text);
                feishuResponse.setUserId(userId);

//                if ("46b551b6".equals(userId)) {
//                    reply(feishuResponse,
//                            "[德胧ChatGPT] 已拒绝与陈观寅对话");
//                    return "";
//                }

                if (text.equals("开启一段新对话") || text.equals("开启新对话")) {
                    log.info("user: {} 开启新对话");
                    clearOrPutNew(userId, context.get(userId));
                    reply(feishuResponse, String.format(
                            "好的，让我们开启新对话吧%n%n[上下文%d/%d]",
                            context.get(userId).size()/2, ChatContextUtils.LIMIT));
                    return "";
                }
                this.log.info("投递用户消息,{}", JSONObject.toJSON(feishuResponse));
                consumer.add(feishuResponse);
                return userId;
            }
            this.log.info("非文本消息 或 发送者不是用户");
        }
        return "";
    }

    private void limitContext(String userId) {
        ArrayList<ChatContext> contextList = context.get(userId);
        if (StringUtils.isEmpty(userId)) {
            log.info("StringUtils.isEmpty(userId): " + contextList.size());
            return;
        }
        if (!CollectionUtils.isEmpty(contextList) &&
                ChatContextUtils.checkContextNum(userId)) {
            log.info("!CollectionUtils.isEmpty(contextList) && " +
                    "ChatContextUtils.checkContextNum(userId): " + contextList.size());
            return;
        }
        clearOrPutNew(userId, contextList);
    }

    private void clearOrPutNew(String userId, ArrayList<ChatContext> contextList) {
        if (!CollectionUtils.isEmpty(contextList)) {
            log.info("clear: " + contextList.size());
            contextList.clear();
        } else {
            contextList = new ArrayList<>();
            context.put(userId, contextList);
            log.info("clearOrPutNew: " + contextList.size());
        }
    }

    private String reply(FeishuResponse poll, String rs) {
        JSONObject params = new JSONObject();
        params.put("uuid", RandomUtil.randomNumbers(10));
        params.put("msg_type", "text");

        JSONObject content = new JSONObject();
        content.put("text", rs);
        params.put("content", content.toJSONString());

        String url = String.format("https://open.feishu.cn/open-apis/im/v1/messages/%s/reply",
                poll.getMessageId());
        String tenantAccessToken = FeishuUtils.getTenantAccessToken();
        String body = null;
        try (HttpResponse authorization = HttpUtil.createPost(url)
                .header("Authorization", "Bearer " + tenantAccessToken)
                .body(params.toJSONString())
                .execute()) {
            body = authorization.body();
        }

        return body;
    }


    private boolean clearCurChatCheckAndDo(String body) {
        return true;
    }
}