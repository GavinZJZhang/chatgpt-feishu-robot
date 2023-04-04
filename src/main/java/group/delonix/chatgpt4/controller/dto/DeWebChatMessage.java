package group.delonix.chatgpt4.controller.dto;

import com.alibaba.fastjson.JSONObject;

public class DeWebChatMessage {
    private String prompt;
    private String systemMessage;
    private JSONObject options;

    public DeWebChatMessage(String prompt, String systemMessage, JSONObject options) {
        this.prompt = prompt;
        this.systemMessage = systemMessage;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }

    public JSONObject getOptions() {
        return options;
    }

    public void setOptions(JSONObject options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "SystemMessage{" +
                "prompt='" + prompt + '\'' +
                ", systemMessage='" + systemMessage + '\'' +
                ", options=" + options +
                '}';
    }
}
