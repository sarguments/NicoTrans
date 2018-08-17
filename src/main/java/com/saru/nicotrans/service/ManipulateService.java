package com.saru.nicotrans.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.Translation;
import com.saru.nicotrans.entity.Contents;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import com.saru.nicotrans.utils.TranslateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.saru.nicotrans.typeAndConfig.ContentType.CONTENT;

@Service
public class ManipulateService {
    private static final Logger log = LoggerFactory.getLogger(ManipulateService.class);
    private final ObjectMapper mapper;

    @Autowired
    public ManipulateService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<Pair> itemsToPairs(List<Item> items) {
        List<Pair> pairList = new ArrayList<>();
        for (Item item : items) {
            Optional.ofNullable(item.findContents()).ifPresent(pairList::add);
        }

        return pairList;
    }

    public String itemsToJson(List<Item> items) {
        String translatedJson = null;

        try {
            translatedJson = mapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            log.debug(e.getMessage());
        }
        return translatedJson;
    }

    public List<String> extractToTranslateTexts(List<Pair> pairList) {
        List<String> toTransList = new ArrayList<>();
        for (Pair pair : pairList) {
            toTransList.add(pair.getContentString());
        }

        return toTransList;
    }

    public List<Translation> translateTexts(List<String> toTransTexts) {
        // 추출한 리스트 번역
        return TranslateUtil.translateList(toTransTexts);
    }

    public void putTranslatedTexts(List<Pair> pairs, List<Translation> translatedTexts) {
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            Contents contents = pair.getContents();
            Translation translation = translatedTexts.get(i);
            contents.put(CONTENT.getName(), translation.getTranslatedText());
        }
    }

    // TODO
    public List<Item> responseJsonToItems(String body) {
        List<Item> items = null;

        try {
            items = mapper.readValue(body,
                    new TypeReference<List<Item>>() {
                    });
        } catch (IOException e) {
            log.debug(e.getMessage());
        }

        return items;
    }

    public String translateResponseJson(String responseJson) {
        List<Item> items = responseJsonToItems(responseJson);

        // 번역할 텍스트의 원본 Content 참조와 텍스트 pairs로 추출
        List<Pair> pairs = itemsToPairs(items);

        // pairs에서 번역할 텍스트만 따로 추출
        List<String> toTransTexts = extractToTranslateTexts(pairs);

        // 번역후 텍스트 다시 put
        List<Translation> translates = translateTexts(toTransTexts);
        putTranslatedTexts(pairs, translates);

        return itemsToJson(items);
    }
}
