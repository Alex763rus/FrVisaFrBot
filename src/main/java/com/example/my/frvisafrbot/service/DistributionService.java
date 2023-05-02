package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.enums.Emoji;
import com.vdurmont.emoji.EmojiParser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.example.my.frvisafrbot.config.BotConfig;

import static com.example.my.frvisafrbot.constant.Constant.NEW_LINE;

@Service
@Slf4j
public class DistributionService {

    private final String PARSE_MODE = "Markdown";
    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private BotConfig botConfig;

    @PostConstruct
    public void init() {
        StringBuilder message = new StringBuilder(EmojiParser.parseToUnicode(Emoji.ROBOT_FACE.getCode())).append(" telegramm bot was started!\n");
        sendMessage(botConfig.getAdminChatId(), message.toString());
    }

    @PreDestroy
    public void squeezyExit() {
        StringBuilder message = new StringBuilder(EmojiParser.parseToUnicode(Emoji.ROBOT_FACE.getCode())).append(" telegramm bot will be *STOPPED*!\n");
        message.append("*Buy!*");
        sendMessage(botConfig.getAdminChatId(), message.toString());
    }

    public void sendTgMessageToChanel(String message) {
        if(message == null){
            return;
        }
        sendMessage(botConfig.getTargetChatId().toString(), message);
    }

    private void sendMessage(String chatId, String message){
        try {
            SendMessage sendMessage = new SendMessage(chatId, message);
            sendMessage.setParseMode(PARSE_MODE);
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
