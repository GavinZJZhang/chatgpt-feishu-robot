package group.delonix.chatgpt4.entity;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zzj
 */
public class ChatContext {
    private String role;
    private String content;

    public ChatContext(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private static final Set<String> x_userSet = Arrays.stream(new String[]{
            "c198b113","userId02"})
            .collect(Collectors.toSet());
    public static boolean existInXUserSet(String userId) {
        return x_userSet.contains(userId);
    }

    @Override
    public String toString() {
        return "ChatContext{" +
                "role='" + role + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
