package com.saru.nicotrans.Utils;

import com.saru.nicotrans.entity.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.saru.nicotrans.ContentType.CHAT;
import static com.saru.nicotrans.ContentType.CONTENT;

public class LogUtil {
    private static final Logger log = LoggerFactory.getLogger(LogUtil.class);

    public static void printTranslateText(List<Item> items) {
        for (Item item: items) {
            Optional.ofNullable(item.get(CHAT.getName()))
                    .ifPresent(chat -> log.info((String) chat.get(CONTENT.getName())));
        }
    }
}
