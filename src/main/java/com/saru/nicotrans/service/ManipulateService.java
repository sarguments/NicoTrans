package com.saru.nicotrans.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.Translation;
import com.saru.nicotrans.entity.Contents;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.saru.nicotrans.typeAndConfig.ContentType.CONTENT;

public class ManipulateService {
    private static final Logger log = LoggerFactory.getLogger(ManipulateService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Pair> itemsToPairs(List<Item> items) {
        List<Pair> pairList = new ArrayList<>();
        for (Item item: items) {
            Optional.ofNullable(item.findContents()).ifPresent(pairList::add);
            log.info("find content : {}", item);
        }

        return pairList;
    }

    public String itemsToJson(List<Item> items) {
        String translatedJson = null;

        try {
            translatedJson = mapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
        }
        return translatedJson;
    }

    public List<String> extractToTranslateTexts(List<Pair> pairList) {
        List<String> toTransList = new ArrayList<>();
        for (Pair pair: pairList) {
            toTransList.add(pair.getMsg());
        }

        return toTransList;
    }

    public void putTranslatedTexts(List<Pair> pairList, List<Translation> translatedList) {
        for (int i = 0; i < pairList.size(); i++) {
            Pair pair = pairList.get(i);
            Contents contents = pair.getContents();
            Translation translation = translatedList.get(i);
            contents.put(CONTENT.getName(), translation.getTranslatedText());
        }
    }
}
