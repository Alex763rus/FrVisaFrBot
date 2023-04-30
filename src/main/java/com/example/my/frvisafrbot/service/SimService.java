package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.config.BotConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class SimService {

    @Autowired
    private BotConfig botConfig;
    private List<String> simList;
    private int pointer;

    @PostConstruct
    public void init() {
        simList = new LinkedList<>();
        simList.add(botConfig.getSim1ApiKey());
        simList.add(botConfig.getSim2ApiKey());
        simList.add(botConfig.getSim3ApiKey());
        resetPointer();
    }

    public String getSim() {
        ++pointer;
        if (pointer > (simList.size() - 1)) {
            resetPointer();
        }
        return simList.get(pointer);
    }

    private void resetPointer() {
        pointer = 0;
    }
}
