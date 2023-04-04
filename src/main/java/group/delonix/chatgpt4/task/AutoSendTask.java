package group.delonix.chatgpt4.task;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import group.delonix.chatgpt4.controller.ChatController;
import group.delonix.chatgpt4.dao.vo.FeishuResponse;
import group.delonix.chatgpt4.entity.ChatContext;
import group.delonix.chatgpt4.utils.ChatContextUtils;
import group.delonix.chatgpt4.utils.FeishuUtils;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zzj
 */
public class AutoSendTask implements Runnable {
    Logger log = LoggerFactory.getLogger(AutoSendTask.class);
    /**
     * chatGPT的keys
     */
    public static String token = System.getenv("OPEN_AI_KEYS");
//            "sk-";
    public static OpenAiService openAiService = null;
    public static ConcurrentLinkedQueue<FeishuResponse> consumer
            = ChatController.consumer;
    public static ConcurrentHashMap<String, ArrayList<ChatContext>> context
            = ChatController.context;

    static {
        openAiService = new OpenAiService(token, Duration.ofSeconds(60));
    }

    @Override
    public void run() {
        while (true) {
            FeishuResponse poll = consumer.poll();
            if (poll == null) {
                log.info("no query,sleep 1.2s");
                try {
                    TimeUnit.MILLISECONDS.sleep(1200);
                } catch (InterruptedException e) {
                    log.error("Thread exception...", e);
                }
                continue;
            }
            ChatContext userChatContext = new ChatContext("user", poll.getQuery());
            try {
                log.info("curQuery use OpenAIkey: {}", token);
                limitContext(poll.getUserId());

                String queryRs = this.query(poll);

                context.get(poll.getUserId())
                        .add(userChatContext);
                context.get(poll.getUserId())
                        .add(new ChatContext("assistant", queryRs));
                String rp = queryRs + String.format("%n%n[上下文%d/%d]",
                        context.get(poll.getUserId()).size()/2, ChatContextUtils.LIMIT);
                this.reply(poll, rp);
//                int randomNum = (int)(Math.random()*2000) + 1200;
                int randomNum = (int)(Math.random()*800) + 500;
                log.info("reply finished, sleep {}ms", randomNum);
                TimeUnit.MILLISECONDS.sleep(randomNum);
            } catch (Exception e) {
                log.error("query service exception...", e);
                context.get(poll.getUserId())
                        .remove(userChatContext);
                this.reply(poll, e.getMessage());
            }
        }
    }

    private String query(FeishuResponse poll) {
        String q = poll.getQuery();
        log.info("开始提问:{}", q);
//        if (containChineseBool(q)) {
//            log.info("提问的语句中包含中文，拒绝此次服务，提问内容为:{}", q);
//            throw new RuntimeException("[德胧ChatGPT] 提问的语句中包含中文，防止账号被停用，拒绝此次服务");
//        }

        StringBuilder sb = new StringBuilder();
        try{
            queryUseChatOrCompletion(poll, sb);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("[德胧ChatGPT] 服务异常，请联系管理员");
        }

        String rs = formatOpenAiResponse(sb);
        log.info("格式化后的rs:{}", rs);
        return rs;
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

    private void queryUseChatOrCompletion(FeishuResponse poll, StringBuilder sb) {
        if (true) {
            queryUseChatCompletion(poll, sb);
        } else {
            queryUseCompletion(poll, sb);
        }
    }

    private void queryUseChatCompletion(FeishuResponse poll, StringBuilder sb) {
        ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
        context.get(poll.getUserId()).forEach(c ->
                messages.add(
                        new ChatMessage(c.getRole(), c.getContent())
                )
        );
        messages.add(new ChatMessage("user", poll.getQuery()));
        log.info("q:{},messages:{}", poll.getQuery(), messages.toString());
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model("gpt-3.5-turbo")
                .maxTokens(1024)
                .build();

        ChatCompletionResult chatCompletion = openAiService.createChatCompletion(chatCompletionRequest);
        /**/
//        ChatCompletionResult chatCompletion = new ChatCompletionResult();
//        List<ChatCompletionChoice> chatCompletionChoiceList = new ArrayList<ChatCompletionChoice>();
//        ChatCompletionChoice chatCompletionChoice01 = new ChatCompletionChoice();
//        ChatCompletionChoice chatCompletionChoice02 = new ChatCompletionChoice();
//        ChatMessage chatMessage01 = new ChatMessage();
//        chatMessage01.setRole("assistant");
//        chatMessage01.setContent(String.format("回复%d第%d句，", 1, 1));
//        chatCompletionChoice01.setMessage(chatMessage01);
//        ChatMessage chatMessage02 = new ChatMessage();
//        chatMessage02.setRole("assistant");
//        chatMessage02.setContent(String.format("回复%d第%d句，", 1, 2));
//        chatCompletionChoice02.setMessage(chatMessage02);
//        chatCompletionChoiceList.add(chatCompletionChoice01);
//        chatCompletionChoiceList.add(chatCompletionChoice02);
//        chatCompletion.setChoices(chatCompletionChoiceList);
        /**/
        log.info("q:{},获取响应:{}", poll.getQuery(), JSONObject.toJSONString(chatCompletion));

        chatCompletion.getChoices().forEach(v -> {
            sb.append(v.getMessage().getContent());
        });
    }

    private void queryUseCompletion(FeishuResponse poll, StringBuilder sb) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(poll.getQuery())
                .model("text-davinci-003")
                .maxTokens(2048)
                .echo(false)
                .build();

        CompletionResult completion = openAiService.createCompletion(completionRequest);
        /**/
//        CompletionResult completion = new CompletionResult();
//        List<CompletionChoice> completionChoiceList = new ArrayList<CompletionChoice>();
//        CompletionChoice completionChoice01 = new CompletionChoice();
//        CompletionChoice completionChoice02 = new CompletionChoice();
//        completionChoice01.setText(String.format("回复%d第%d句，", 1, 1));
//        completionChoice02.setText(String.format("回复%d第%d句", 1, 2));
//        completionChoiceList.add(completionChoice01);
//        completionChoiceList.add(completionChoice02);
//        completion.setChoices(completionChoiceList);
        /**/
        log.info("q:{},获取响应:{}", poll.getQuery(), JSONObject.toJSONString(completion));

        completion.getChoices().forEach(v -> {
            sb.append(v.getText());
        });
    }

    private String formatOpenAiResponse(StringBuilder sb) {
        String rs = sb.toString();
        if (rs.startsWith("？")) {
            rs = rs.replaceFirst("？", "");
        }
        if (rs.startsWith("?")) {
            rs = rs.replaceFirst("[?]", "");
        }
        if (rs.startsWith("\n\n")) {
            rs = rs.replaceFirst("\n\n", "");
        }
        return rs;
    }

    public boolean containChineseBool(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    private void limitContext(String userId) {
        ArrayList<ChatContext> contextList = context.get(userId);
        if (StringUtils.isEmpty(userId)) {
            return;
        }
        if (!CollectionUtils.isEmpty(contextList) &&
                ChatContextUtils.checkContextNum(userId)) {
            return;
        }
        clearOrPutNew(userId, contextList);
    }

    private void clearOrPutNew(String userId, ArrayList<ChatContext> contextList) {
        if (!CollectionUtils.isEmpty(contextList)) {
            contextList.clear();
        } else {
            contextList = new ArrayList<>();
            context.put(userId, contextList);
        }
    }
//    /**
//     * 限制一轮对话的上下文个数
//     * @param userId
//     */
//    private void limitContext(String userId) {
//        ArrayList<ChatContext> contextList = context.get(userId);
//        if (StringUtils.isEmpty(userId)) {
//            return;
//        }
//        if (!CollectionUtils.isEmpty(contextList) &&
//                ContextUtils.checkContextNum(userId)) {
//            return;
//        }
//        contextList = new ArrayList<ChatContext>();
//        context.put(userId, contextList);
//    }

}
