package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.api.CheckUpdate;
import com.example.my.frvisafrbot.config.BotConfig;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import java.util.List;

@Service
@Slf4j
public class CheckUpdateService implements Runnable {

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private SrcAnalysisService srcAnalysisService;

    @Autowired
    private DelayService delayService;

    @PostConstruct
    public void init() {
        Thread thread = new Thread(this::run);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        try {
            List<String> newMessages;
            Long delay;
            while (true) {
                log.info("Start post request process.");
                newMessages = srcAnalysisService.getNewMessages();
                for (String message : newMessages) {
                    distributionService.sendTgMessageToChanel(message);
                }
                newMessages.clear();
                delay = delayService.getDelay();
                log.info("End post request process. Next post request after:" + delayService.getDelay());
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
