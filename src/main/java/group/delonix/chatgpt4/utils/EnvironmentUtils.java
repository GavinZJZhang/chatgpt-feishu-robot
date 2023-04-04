package group.delonix.chatgpt4.utils;

import group.delonix.chatgpt4.controller.ChatController;
import group.delonix.chatgpt4.task.AutoSendTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author zzj
 */
@Configuration
public class EnvironmentUtils implements EnvironmentAware {
    Logger log = LoggerFactory.getLogger(EnvironmentUtils.class);
    public static String openAiKeys = "";
    public static final String staticOpenAiKeys = System.getenv("OPEN_AI_KEYS");

    @Override
    public void setEnvironment(Environment environment) {
        openAiKeys = environment.getProperty("OPEN_AI_KEYS");
//        log.info("OpenAI keys: {}",openAiKeys);
    }

}
