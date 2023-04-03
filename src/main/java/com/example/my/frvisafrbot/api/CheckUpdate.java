package com.example.my.frvisafrbot.api;
import com.example.my.frvisafrbot.config.BotConfig;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
public class CheckUpdate {
    private String api_key;
    private String type;
    private String chat_id;
    private int limit;
    private int offset;
    private Long from_message_id;

    @PostConstruct
    public void init(){
        type = "getChatHistory";
        setLimit(10);
        setOffset(0);
        setFrom_message_id(0L);
    }
}