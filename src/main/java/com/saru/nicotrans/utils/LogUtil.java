package com.saru.nicotrans.utils;

import com.saru.nicotrans.entity.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.saru.nicotrans.typeAndConfig.ContentType.CHAT;
import static com.saru.nicotrans.typeAndConfig.ContentType.CONTENT;

public class LogUtil {
    private static final Logger log = LoggerFactory.getLogger(LogUtil.class);

    public static void printTranslateText(List<Item> items) {
        for (Item item : items) {
            Optional.ofNullable(item.get(CHAT.getName()))
                    .ifPresent(chat -> log.debug((String) chat.get(CONTENT.getName())));
        }
    }

    public static void printRequestHeahder(@RequestHeader HttpHeaders httpHeaders) {
        // 요청 헤더 출력
        Set<Map.Entry<String, List<String>>> headers = httpHeaders.entrySet();
        for (Map.Entry<String, List<String>> header : headers) {
            log.info("key : value = {} : {}", header.getKey(), header.getValue());
        }
    }
}
