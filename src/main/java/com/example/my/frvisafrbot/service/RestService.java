package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.api.CheckUpdate;
import com.example.my.frvisafrbot.config.BotConfig;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;

@Component
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
    //        String requestBody = "{\n" +
//                "  \"api_key\": \"79992878738:yq55NgdyBrzPTfz85Qs1Dy3dSkLNEHLQx_z2pTJc\",\n" +
//                "  \"@type\": \"getChatHistory\",\n" +
//                "  \"chat_id\": \"-1001612884083\",\n" +
//                "  \"limit\": \"2\",\n" +
//                "  \"offset\": \"0\",\n" +
//                "  \"from_message_id\": \"0\"\n" +
//                "}";
    public String sendPost(String simApiKey) {
        checkUpdate.setApi_key(simApiKey);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String json = (new ObjectMapper().writeValueAsString(checkUpdate)).replace("\"type\"", "\"@type\"");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
            ResponseEntity<String> responseEntity = restTemplate
                    .exchange(URL, HttpMethod.POST, httpEntity, String.class);
            return responseEntity.getBody();
        } catch (JsonGenerationException | JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
