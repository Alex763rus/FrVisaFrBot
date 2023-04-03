package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.config.BotConfig;
import com.example.my.frvisafrbot.model.jpa.User;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private MainMenuService mainMenuService;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        try {
            execute(new SetMyCommands(mainMenuService.getMainMenuComands(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
        log.info("==" + "Server was starded. Version: " + botConfig.getBotVersion() + "==================================================================================================");
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        PartialBotApiMethod answer;
        Message message;
        if (update.hasMessage()) {
            message = update.getMessage();
        } else if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
        } else {
            log.warn("Сообщение не содержит текста и нажатия на кнопку...");
            return;
        }
        User user = userService.getUser(message);
        if (user == null) {
            log.warn("Пользователь отсутствует в белом списке, ответа не получит:" + message.getChatId());
            return;
        }
        answer = mainMenuService.messageProcess(user, update);
        try {
            if (answer instanceof BotApiMethod) {
                execute((BotApiMethod) answer);
            }
            if (answer instanceof SendDocument) {
                execute((SendDocument) answer);
            }
        } catch (TelegramApiException e) {
            log.error("Ошибка во время обработки сообщения: " + e.getMessage());
        }

    }
}
