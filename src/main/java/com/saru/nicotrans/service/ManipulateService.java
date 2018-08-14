package com.saru.nicotrans.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.Translation;
import com.saru.nicotrans.entity.Contents;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.saru.nicotrans.typeAndConfig.ContentType.CONTENT;

@Service
public class ManipulateService {
    private static final Logger log = LoggerFactory.getLogger(ManipulateService.class);
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    // TODO 컨트롤러에 있는 로직 서비스로 옮겨야 함

    @Autowired
    public ManipulateService(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

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

    public void putTranslatedTexts(List<Pair> pairs, List<Translation> transTexts) {
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            Contents contents = pair.getContents();
            Translation translation = transTexts.get(i);
            contents.put(CONTENT.getName(), translation.getTranslatedText());
        }
    }

    public List<Item> responseJsonToItems(String response) {
        List<Item> items = null;

        try {
            items = mapper.readValue(response,
                    new TypeReference<List<Item>>() {
                    });
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return items;
    }
}
