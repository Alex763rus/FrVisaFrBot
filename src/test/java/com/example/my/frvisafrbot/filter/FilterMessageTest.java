package com.example.my.frvisafrbot.filter;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static com.example.my.frvisafrbot.constant.Constant.NEW_LINE;
import static com.example.my.frvisafrbot.constant.Constant.STAR;

public class FilterMessageTest {

    private final String INPUT_MESSAGE_OK = "\uD83D\uDD14 САНКТ-ПЕТЕРБУРГ\n" +
            NEW_LINE +
            "\uD83D\uDFE2 Short Stay All kind of other short stay visas\n" +
            NEW_LINE +
            "Ближайшая дата: 14 апреля 2023 г.\n" +
            NEW_LINE +
            "ЗАПИСАТЬСЯ НА САЙТЕ VFS\n" +
            " (https://www.vfsvisaservicesrussia.com/Global-Appointment/Account/RegisteredLogin?q=shSA0YnE4pLF9Xzwon/x/BGxVUxGuaZP3eMAtGHiEL0kQAXm+Lc2PfVNUJtzf7vWRu19bwvTWMZ48njgDU5r4g==)ПОЛЕЗНАЯ ИНФОРМАЦИЯ\n" +
            " (https://t.me/vfs_france/1814)ОТПРАВИТЬ ДОНАТ\uD83D\uDC4D\n" +
            NEW_LINE +
            " (https://pay.mysbertips.ru/06030299)Telegram\n" +
            " (https://t.me/vfs_france/1814)Бот записи на визу Франции (VFS France Monitoring)\n" +
            "Друзья, всем привет! \uD83D\uDC4B \n" +
            "Собрал в одном месте всю полезную информацию и сервисы для подписчиков канала @vfs_france";
    private final String EXPECTED_RESULT_OK = "*САНКТ-ПЕТЕРБУРГ*\n" +
            "*Short* Stay All kind of other short stay visas\n" +
            "Ближайшая дата: 14 апреля 2023 г.\n" +
            "[ЗАПИСАТЬСЯ НА САЙТЕ VFS](https://www.vfsvisaservicesrussia.com/Global-Appointment/Account/RegisteredLogin?q=shSA0YnE4pLF9Xzwon/x/BGxVUxGuaZP3eMAtGHiEL0kQAXm+Lc2PfVNUJtzf7vWRu19bwvTWMZ48njgDU5r4g)";


    private final String INPUT_MESSAGE_ERROR = "⚠️ Друзья VFS поменял сайт для записи. Записывайтесь на сайте\n" +
            "https://visa.vfsglobal.com/rus/en/fra/login\n" +
            NEW_LINE +
            "Новый сайт требует оплату сбора визового центра в момент записи. \n" +
            NEW_LINE +
            "Отслеживание на новом сайте постараемся начать как можно скорее.";


    @Test
    public void getPrepareMessageTest() {
        Assertions.assertEquals(EXPECTED_RESULT_OK, getPrepareMessage(INPUT_MESSAGE_OK));
        Assertions.assertNull(getPrepareMessage(INPUT_MESSAGE_ERROR));
    }

    private String getPrepareMessage(String message) {
        if (checkMessageContent(message) == null) {
            return null;
        }
        val result = new StringBuilder();
        val tmp = new StringBuilder();
        try {
            val excludeSimbol = Arrays.asList("🔔", "🟢", "⚠️");
            val replaceSimbol = Map.of(NEW_LINE + NEW_LINE, NEW_LINE, "Short", STAR + "Short" + STAR);
            val whiteList = Arrays.asList("Short", "Ближайшая дата");

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
            result.append("[ЗАПИСАТЬСЯ НА САЙТЕ VFS](https://www.vfsvisaservicesrussia.com/Global-Appointment/Account/RegisteredLogin?q=shSA0YnE4pLF9Xzwon/x/BGxVUxGuaZP3eMAtGHiEL0kQAXm+Lc2PfVNUJtzf7vWRu19bwvTWMZ48njgDU5r4g)");
        } catch (Exception ex) {
            int x = 0;
        }
        return result.toString();
    }

    private String checkMessageContent(String message) {
        val whiteList = Arrays.asList("Short", "Ближайшая дата");
        //Проверка сообщения на ключевые слова
        for (String whiteListWord : whiteList) {
            if (message.indexOf(whiteListWord) == -1) {
                return null;
            }
        }
        return message;
    }

}
