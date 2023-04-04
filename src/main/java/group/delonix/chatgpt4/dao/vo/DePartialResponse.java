package group.delonix.chatgpt4.dao.vo;

/**
 * @author zzj
 */
public class DePartialResponse {
    private String role;
    private String id;
    private String parentMessageId;
    private String conversationId;
    private String text;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(String parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "DePartialResponse{" +
                "role='" + role + '\'' +
                ", id='" + id + '\'' +
                ", parentMessageId='" + parentMessageId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
