package com.example.my.frvisafrbot.config;

import com.example.my.frvisafrbot.api.CheckUpdate;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Data
@PropertySource("application.properties")
@EnableWebMvc
@EnableTransactionManagement
public class BotConfig {

    @Value("${bot.version}")
    String botVersion;

    @Value("${bot.username}")
    String botUserName;

    @Value("${source.chatid}")
    String sourceChatId;

    @Value("${target.chatid}")
    String targetChatId;

    @Value("${admin.chatid}")
    String adminChatId;

    @Value("${bot.token}")
    String botToken;

    @Value("${sim1.api.key}")
    String sim1ApiKey;
    @Value("${sim2.api.key}")
    String sim2ApiKey;
    @Value("${sim3.api.key}")
    String sim3ApiKey;
    @Value("${sim4.api.key}")
    String sim4ApiKey;
    @Value("${sim5.api.key}")
    String sim5ApiKey;


    @Value("${day.delay}")
    Long dayDelay;
    @Value("${day.start.hour}")
    Integer dayStartHour;
    @Value("${night.delay}")
    Long nightDelay;
    @Value("${night.start.hour}")
    Integer nightStartHour;
}
