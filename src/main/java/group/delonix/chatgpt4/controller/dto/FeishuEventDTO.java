package group.delonix.chatgpt4.controller.dto;

/**
 * @author
 */
public class FeishuEventDTO {
    private String challenge;

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    @Override
    public String toString() {
        return "FeishuEventDTO{" +
                "challenge='" + challenge + '\'' +
                '}';
    }
}
