package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.my.frvisafrbot.constant.Constant.NEW_LINE;
import static com.example.my.frvisafrbot.constant.Constant.STAR;

@Service
@Slf4j
public class SrcAnalysisService {

    @Autowired
    private RestService restService;

    @Autowired
    private SimService simService;

    @Autowired
    private BotConfig botConfig;


    private Long lastSpamMessageTime;
    private long lastMessageId;

    private String lastMessage;

    public List<String> getNewMessages() {
        String jsonData = restService.sendPost(simService.getSim());
        List<String> newMessages = getFilteredMessage(jsonData);
        log.info("Count messages after filter:" + newMessages.size());
        List<String> preparedMessages = new ArrayList<>();
        for (String message : newMessages) {
            preparedMessages.add(getPrepareMessage(message));
        }
        return preparedMessages;
    }

    private final List<String> whiteList = Arrays.asList("Short", "Ближайшая дата");

    private String getPrepareMessage(String message) {
        if (checkMessageContent(message) == null) {
            return null;
        }
        val result = new StringBuilder();
        val tmp = new StringBuilder();
        try {
            val excludeSimbol = Arrays.asList("🔔", "🟢", "⚠️");
            val replaceSimbol = Map.of(NEW_LINE + NEW_LINE, NEW_LINE, "Short", STAR + "Short" + STAR);
            //Заменяем найденное
            for (Map.Entry<String, String> entry : replaceSimbol.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
            tmp.append(message);
            //Удаляем ненужное
            for (String exclude : excludeSimbol) {
                int indexStart = tmp.indexOf(exclude);
                if (indexStart == -1) {
                    continue;
                }
                tmp.delete(indexStart, indexStart + exclude.length() + 1);
            }
            val lines = tmp.toString().split(NEW_LINE);
            boolean isFirst = true;
            for (String line : lines) {
                if (isFirst) {
                    result.append(STAR).append(line).append(STAR).append(NEW_LINE);
                    isFirst = false;
                }
                for (String whiteListWord : whiteList) {
                    if (line.contains(whiteListWord)) {
                        result.append(line).append(NEW_LINE);
                    }
                }
            }
            result.append(botConfig.getMessageLink());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result.toString();
    }

    private List<String> getFilteredMessage(String jsonData) {
        List<String> newMessages = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            int messageCount = jsonObject.getInt("total_count");
            log.info("Count messages before filter:" + messageCount);
            if (messageCount == 0) {
                return null;
            }
            long tmpLastMessageId = 0;
            for (int i = 0; i < messageCount; ++i) {
                long tmpMessageId = jsonObject.getJSONArray("messages").getJSONObject(i).getLong("id");
                String message = jsonObject.getJSONArray("messages").getJSONObject(i).getJSONObject("content")
                        .getJSONObject("text").getString("text");
                if (lastMessageId == 0) {
                    log.info("lastMessageId = 0. This is start");
                    lastMessageId = tmpMessageId;
                    lastMessage = message;
                    lastSpamMessageTime = System.currentTimeMillis();
                    return newMessages;
                }
                if (i == 0) {
                    tmpLastMessageId = tmpMessageId;
                    lastMessage = message;
                }
                if (tmpMessageId == lastMessageId
                        || (lastMessage.equals(message) && ((System.currentTimeMillis() - lastSpamMessageTime) < botConfig.getSpamDelay()))) {
                    log.info("tmpMessageId == lastMessageId, or message equals lastMessage. tmpMessageId=" + tmpMessageId + ", lastMessageId=" + lastMessageId);
                    break;
                }
                lastSpamMessageTime = System.currentTimeMillis();
                if (message != null) {
                    newMessages.add(message);
                } else {
                    log.info("Did not pass the test checkMessageContent");
                }
            }
            lastMessageId = tmpLastMessageId;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return newMessages;
    }

    private String checkMessageContent(String message) {
        //Проверка сообщения на ключевые слова
        for (String whiteListWord : whiteList) {
            if (message.indexOf(whiteListWord) == -1) {
                return null;
            }
        }
        return message;
    }
}
