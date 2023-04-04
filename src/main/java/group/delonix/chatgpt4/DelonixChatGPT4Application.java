package group.delonix.chatgpt4;

import group.delonix.chatgpt4.task.AutoSendTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @author zzj
 */
@SpringBootApplication
public class DelonixChatGPT4Application {
    //启动类
    public static void main(String[] args) {
        SpringApplication.run(DelonixChatGPT4Application.class, args);
    }

    @PostConstruct
    public void init() {
        Thread thread = new Thread(new AutoSendTask());
        thread.start();

        Thread thread2 = new Thread(new AutoSendTask());
        thread2.start();

        Thread thread3 = new Thread(new AutoSendTask());
        thread3.start();

        Thread thread4 = new Thread(new AutoSendTask());
        thread4.start();

        /*
        多线程会把CPU打满，导致服务器死机
        Thread thread5 = new Thread(new AutoSendTask());
        thread5.start();

        Thread thread6 = new Thread(new AutoSendTask());
        thread6.start();

        Thread thread7 = new Thread(new AutoSendTask());
        thread7.start();

        Thread thread8 = new Thread(new AutoSendTask());
        thread8.start();
        */
    }

}
