package group.delonix.chatgpt4.controller.dto;

/**
 * @author
 */
public class FeishuEventParams {

    private String challenge;
    private String token;
    private String type;

    @Override
    public String toString() {
        return "FeishuEventParams{" +
                "challenge='" + challenge + '\'' +
                ", token='" + token + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
