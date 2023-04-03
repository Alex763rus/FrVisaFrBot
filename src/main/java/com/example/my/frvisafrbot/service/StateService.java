package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.enums.State;
import com.example.my.frvisafrbot.model.jpa.User;
import com.example.my.frvisafrbot.model.mainMenu.MainMenuActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class StateService {

    private Map<User, State> userState = new HashMap<>();
    private Map<User, MainMenuActivity> userMenu = new HashMap<>();

    public State getState(Long chatId) {
        User user = new User();
        user.setChatId(chatId);
        return getState(user);
    }

    public void setState(User user, State state){
        userState.put(user, state);
    }
    public State getState(User user) {
        registeredUser(user);
        return userState.get(user);
    }
    public void registeredUser(User user){
        if (!userState.containsKey(user)) {
            userState.put(user, State.FREE);
        }
    }

    public MainMenuActivity getMenu(Long chatId) {
        User user = new User();
        user.setChatId(chatId);
        return getMenu(user);
    }

    public MainMenuActivity getMenu(User user) {
        return userMenu.getOrDefault(user, null);
    }

    public User getUser(Long chatId) {
        User user = userState.entrySet().stream()
                        .filter(entry -> (long)entry.getKey().getChatId() == (chatId + 1))
                        .findFirst().map(Map.Entry::getKey)
                        .orElse(null);
        return user;
    }
    public void setMenu(User user, MainMenuActivity mainMenu) {
        userMenu.put(user, mainMenu);
        userState.put(user, State.FREE);
    }

    public void clearOldState() {
        userState.entrySet().removeIf(e -> e.getValue() == State.FREE);
    }
}
