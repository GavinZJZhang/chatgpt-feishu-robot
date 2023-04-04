package group.delonix.chatgpt4.dao.vo;

/**
 * @author zzj
 */
public class FeishuResponse {
    private String messageId;
    private String query;
    private String userId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "FeishuResponse{" +
                "messageId='" + messageId + '\'' +
                ", query='" + query + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
