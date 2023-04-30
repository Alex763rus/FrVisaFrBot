package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.api.CheckUpdate;
import com.example.my.frvisafrbot.config.BotConfig;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;

@Component
@Slf4j
public class RestService {

    private final String URL = "https://api.tdlib.org/client";
    @Autowired
    BotConfig botConfig;

    @Autowired
    CheckUpdate checkUpdate;

    @PostConstruct
    public void init() {
        checkUpdate.setChat_id(botConfig.getSourceChatId());
    }

    public String sendPost(String simApiKey) {
        checkUpdate.setApi_key(simApiKey);
        RestTemplate restTemplate = new RestTemplate();
        try {
            log.info("Will be executed request from user:" + simApiKey);
            String json = (new ObjectMapper().writeValueAsString(checkUpdate)).replace("\"type\"", "\"@type\"");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
            ResponseEntity<String> responseEntity = restTemplate
                    .exchange(URL, HttpMethod.POST, httpEntity, String.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
