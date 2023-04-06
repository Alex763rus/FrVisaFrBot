package com.example.my.frvisafrbot.service;

import com.example.my.frvisafrbot.config.BotConfig;
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

    private String getPrepareMessage(String message) {
            StringBuilder sb = new StringBuilder(message);
        try {
            ArrayList<String> excludeSimbol = new ArrayList<>();
            excludeSimbol.add("🔔");
            excludeSimbol.add("🟢");
            excludeSimbol.add("⚠️");
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

            String sShort = "Short";
            String star = "*";
            if(sb.indexOf(sShort)!=-1) {
                sb.replace(0, sb.indexOf(sShort) + sShort.length(), star + sb.substring(0, sb.indexOf(sShort) + sShort.length()) + star);
            }
            String writeOnSite = "ЗАПИСАТЬСЯ НА САЙТЕ VFS";
            if(sb.indexOf(writeOnSite)!=-1) {
                sb.delete(sb.indexOf(writeOnSite), sb.length());
            }
            sb.append("[ЗАПИСАТЬСЯ НА САЙТЕ VFS](https://www.vfsvisaservicesrussia.com/Global-Appointment/Account/RegisteredLogin?q=shSA0YnE4pLF9Xzwon/x/BGxVUxGuaZP3eMAtGHiEL0kQAXm+Lc2PfVNUJtzf7vWRu19bwvTWMZ48njgDU5r4g)");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return sb.toString();
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
                newMessages.add(message);
            }
            lastMessageId = tmpLastMessageId;
        } catch(Exception ex){
            log.error(ex.getMessage());
        }
        return newMessages;
    }
}
