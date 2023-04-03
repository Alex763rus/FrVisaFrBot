package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.model.jpa.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;

@Slf4j
@Service
@Data
public class UserService {

    @Autowired
    private StateService stateService;

//    @Autowired
//    private WhiteListUserConfig whiteListUsers;

    public User getUser(Message message){
        Long chatId = message.getChatId();
        User user = stateService.getUser(chatId);
        if(user == null){
//            if(!whiteListUsers.getWhiteListChatsID().contains(chatId)){
//                return null;
//            }
            user = createUser(message);
            stateService.registeredUser(user);
        }
        return user;
    }

    private User createUser(Message message) {
        var chatId = message.getChatId();
        var chat = message.getChat();
        User user = new User();
        user.setChatId(chatId);
        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setUserName(chat.getUserName());
        user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
        return user;
    }
}