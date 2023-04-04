package group.delonix.chatgpt4.utils;

import group.delonix.chatgpt4.controller.ChatController;
import group.delonix.chatgpt4.entity.ChatContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author zzj
 */
public class ChatContextUtils {
    static Logger log = LoggerFactory.getLogger(ChatContextUtils.class);
    public static final int LIMIT = 10;
//    private static final int X_LIMIT = 10;
//    private static final int XX_LIMIT = 15;


    public static boolean checkContextNum(String userId) {
        return ChatController.context.get(userId).size() < 2*LIMIT
                ? true : false;
//        return ChatController.context.get(userId).size()
//                < (ChatContext.existInXUserSet(userId) ? 2*X_LIMIT : 2*LIMIT)
//                ? true : false;
    }

//    public static void main(String[] args) {
//        ChatContext chatContext01 = new ChatContext("user", "text01");
//        ChatContext chatContext02 = new ChatContext("assistant", "reply01");
//        ChatContext chatContext03 = new ChatContext("user", "text02");
//        ChatContext chatContext04 = new ChatContext("assistant", "reply02");
//        ArrayList<ChatContext> chatContextArrayList = new ArrayList<>();
//        chatContextArrayList.add(chatContext01);
//        chatContextArrayList.add(chatContext02);
//        chatContextArrayList.add(chatContext03);
//        chatContextArrayList.add(chatContext04);
//        System.out.println(chatContextArrayList.toString());
//    }

    public static String formatOpenAiResponse(StringBuilder sb) {
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
}
