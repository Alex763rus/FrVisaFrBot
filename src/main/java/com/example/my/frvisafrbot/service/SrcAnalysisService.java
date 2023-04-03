package com.example.my.frvisafrbot.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SrcAnalysisService {

    @Autowired
    private RestService restService;

    @Autowired
    private SimService simService;
    private long lastMessageId;


    public List<String> getNewMessages() {
        String jsonData = restService.sendPost(simService.getSim());
        List<String> newMessages = getFilteredMessage(jsonData);
        if (newMessages.size() == 0) {
            log.info("New message not a found. Wait...");
        }
        List<String> preparedMessages = new ArrayList<>();
        for (String message : newMessages) {
            preparedMessages.add(getPrepareMessage(message));
        }
        return preparedMessages;
    }

    private String getPrepareMessage(String message) {
            StringBuilder sb = new StringBuilder(message);
        try {
            ArrayList<String> excludeSimbol = new ArrayList<>();
            excludeSimbol.add("🔔");
            excludeSimbol.add("🟢");
            for (String exclude : excludeSimbol) {
                int indexStart = sb.indexOf(exclude);
                if (indexStart == -1) {
                    continue;
                }
                sb.delete(indexStart, indexStart + exclude.length() + 1);
            }
            String searchStr = "\n\n";
            while (true) {
                int index = sb.indexOf(searchStr);
                if (index == -1) {
                    break;
                }
                sb.replace(index, index + searchStr.length(), "\n");
            }

            sb.replace(0, sb.indexOf("Short") + 5, "*" + sb.substring(0, sb.indexOf("Short") + 5) + "*");
            sb.delete(sb.indexOf("ЗАПИСАТЬСЯ НА САЙТЕ VFS"), sb.length());
            sb.append("[ЗАПИСАТЬСЯ НА САЙТЕ VFS](https://www.vfsvisaservicesrussia.com/Global-Appointment/Account/RegisteredLogin?q=shSA0YnE4pLF9Xzwon/x/BGxVUxGuaZP3eMAtGHiEL0kQAXm+Lc2PfVNUJtzf7vWRu19bwvTWMZ48njgDU5r4g)");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return sb.toString();
    }

    private List<String> getFilteredMessage(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        int messageCount = jsonObject.getInt("total_count");
        if (messageCount == 0) {
            return null;
        }
        List<String> newMessages = new ArrayList<>();
        long tmpLastMessageId = 0;

        for (int i = 0; i < messageCount; ++i) {
            long tmpMessageId = jsonObject.getJSONArray("messages").getJSONObject(i).getLong("id");
            if (lastMessageId == 0) {
                lastMessageId = tmpMessageId;
                return newMessages;
            }
            if (i == 0) {
                tmpLastMessageId = tmpMessageId;
            }
            if (tmpMessageId == lastMessageId) {
                break;
            }
            newMessages.add(jsonObject.getJSONArray("messages").getJSONObject(i).getJSONObject("content")
                    .getJSONObject("text").getString("text"));
        }
        lastMessageId = tmpLastMessageId;
        return newMessages;
    }
}
