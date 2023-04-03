package com.example.my.frvisafrbot.model.mainMenu;

import com.example.my.frvisafrbot.service.StateService;
import com.example.my.frvisafrbot.service.UserService;
import jakarta.persistence.MappedSuperclass;
import org.springframework.beans.factory.annotation.Autowired;


@MappedSuperclass
public abstract class MainMenu implements MainMenuActivity {

    @Autowired
    protected UserService userService;

    @Autowired
    protected StateService stateService;

}
