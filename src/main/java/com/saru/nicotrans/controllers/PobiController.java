package com.saru.nicotrans.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.Translation;
import com.saru.nicotrans.Pair;
import com.saru.nicotrans.TranslateUtil;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@RestController
public class PobiController {
    private static final String CONTENT = "content";
    private static final String CHAT = "chat";
    private static final String COMMENT_SERVER_URL = "http://pobi.god/api.json";

    private static final Logger log = LoggerFactory.getLogger(PobiController.class);
    private ObjectMapper mapper = new ObjectMapper();

    @GetMapping("")
    public String welcome() {
        log.info("WOW!");

        return "Hello";
    }

    @PostMapping("/api.json")
    public ResponseEntity<String> postWelcome(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestBody String json) {
        Set<Map.Entry<String, List<String>>> headers = httpHeaders.entrySet();
        for (Map.Entry<String, List<String>> header : headers) {
            log.info("key : value = {} : {}", header.getKey(), header.getValue());
        }

        // 요청 헤더 생성 후 json과 같이 httpEntity 조합
        HttpEntity<String> httpEntity = new HttpEntity<>(json, makeHeaders(httpHeaders));

        // gzip으로 인해 아파치 라이브러리 사용 - HttpComponentsClientHttpRequestFactory
        RestTemplate restTemplate = makeRestTemplate();

        // json 얻어온다
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(COMMENT_SERVER_URL, httpEntity, String.class);
        log.info(responseEntity.getBody());

        //-------------------------------------------------------------------------------

        // json to Items
        List<Item> items = responseJsonToItems(responseEntity);

        // 번역할 텍스트의 원본 Content 참조와 텍스트 pairs로 추출
        List<Pair> pairs = itemsToPairs(items);
        log.info("pairList size : {}", pairs.size());

        // pairs에서 번역할 텍스트만 따로 추출
        List<String> toTransTexts = extractToTranslateTexts(pairs);
        log.info("toTransList size : {}", toTransTexts.size());

        // 추출한 리스트 번역
        List<Translation> translatedTexts = TranslateUtil.translateList(toTransTexts);
        log.info("translatedList size : {}", translatedTexts.size());

        // 번역 텍스트 다시 put
        putTranslatedTexts(pairs, translatedTexts);

        // 테스트 번역 텍스트 출력
        printTranslateText(items);

        //-------------------------------------------------------------------------------

        // object To Json
        String translatedJson = itemsToJson(items);

        // 테스트 json 출력
        log.info(translatedJson);

        // 응답 content type 설정 후 브라우저로 ResponseEntity 리턴
        return new ResponseEntity<>(translatedJson, makeResponseHeaders(), HttpStatus.OK);
    }


    private HttpHeaders makeResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "text/json; charset=UTF-8");
        return responseHeaders;
    }

    private RestTemplate makeRestTemplate() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        return new RestTemplate(clientHttpRequestFactory);
    }

    private void printTranslateText(List<Item> items) {
        for (Item item : items) {
            Optional.ofNullable(item.get(CHAT)).ifPresent(chat -> log.info((String) chat.get(CONTENT)));
        }
    }

    private HttpHeaders makeHeaders(HttpHeaders httpHeaders) {
        HttpHeaders toRequestHeaders = new HttpHeaders();

        toRequestHeaders.setAccept(httpHeaders.getAccept());
        toRequestHeaders.set(HttpHeaders.ACCEPT_ENCODING, httpHeaders.get(HttpHeaders.ACCEPT_ENCODING).toString());
        toRequestHeaders.setAcceptLanguage(httpHeaders.getAcceptLanguage());
        toRequestHeaders.setCacheControl(httpHeaders.getCacheControl());
        toRequestHeaders.setConnection(httpHeaders.getConnection());
        toRequestHeaders.setContentType(httpHeaders.getContentType());
        toRequestHeaders.setHost(httpHeaders.getHost());
        toRequestHeaders.setOrigin(httpHeaders.getOrigin());
        toRequestHeaders.setPragma(httpHeaders.getPragma());
        toRequestHeaders.set(HttpHeaders.REFERER, httpHeaders.get(HttpHeaders.REFERER).toString());
        toRequestHeaders.set(HttpHeaders.USER_AGENT, httpHeaders.get(HttpHeaders.USER_AGENT).toString());

        return toRequestHeaders;
    }

    private String itemsToJson(List<Item> items) {
        String translatedJson = null;

        try {
            translatedJson = mapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
        }
        return translatedJson;
    }

    private List<Item> responseJsonToItems(ResponseEntity<String> responseEntity) {
        List<Item> items = null;

        try {
            items = mapper.readValue(responseEntity.getBody(),
                    new TypeReference<List<Item>>() {
                    });
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return items;
    }

    private List<Pair> itemsToPairs(List<Item> items) {
        List<Pair> pairList = new ArrayList<>();
        for (Item item : items) {
            Optional.ofNullable(item.findContents()).ifPresent(pairList::add);
            log.info("find content : {}", item);
        }

        return pairList;
    }

    private List<String> extractToTranslateTexts(List<Pair> pairList) {
        List<String> toTransList = new ArrayList<>();
        for (Pair pair : pairList) {
            toTransList.add(pair.getMsg());
        }

        return toTransList;
    }

    private void putTranslatedTexts(List<Pair> pairList, List<Translation> translatedList) {
        for (int i = 0; i < pairList.size(); i++) {
            pairList.get(i).getContents().put(CONTENT, translatedList.get(i).getTranslatedText());
        }
    }

    private static class Contents extends HashMap<String, Object> {
        Pair findContent() {
            for (String s : keySet()) {
                if (s.equals(CONTENT)) {
                    String originalText = (String) get(CONTENT);
                    return new Pair(this, originalText);
                }
            }

            return null;
        }
    }

    private static class Item extends HashMap<String, Contents> {
        Pair findContents() {
            Set<Map.Entry<String, Contents>> itemSets = entrySet();
            for (Map.Entry<String, Contents> itemSet : itemSets) {
                if (itemSet.getKey().equals(CHAT)) {
                    Contents contents = itemSet.getValue();
                    return contents.findContent();
                }
            }

            return null;
        }
    }
}
