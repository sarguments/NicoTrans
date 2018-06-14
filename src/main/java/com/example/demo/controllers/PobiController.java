package com.example.demo.controllers;

import com.example.demo.Pair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
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
    private static final Logger log = LoggerFactory.getLogger(PobiController.class);
    private static Translate translate = TranslateOptions.newBuilder().build().getService();
    private ObjectMapper mapper = new ObjectMapper();

    @GetMapping("")
    public String welcome() {
        log.info("WOW!");

        return "Hello";
    }

    private static Translation translateTextWithOptions(String sourceText) {
        Translate.TranslateOption srcLang = Translate.TranslateOption.sourceLanguage("ja");
        Translate.TranslateOption tgtLang = Translate.TranslateOption.targetLanguage("ko");
        return translate.translate(sourceText, srcLang, tgtLang);
    }

    private static List<Translation> translationsList(List<String> sourceTexts) {
        Translate.TranslateOption srcLang = Translate.TranslateOption.sourceLanguage("ja");
        Translate.TranslateOption tgtLang = Translate.TranslateOption.targetLanguage("ko");
        return translate.translate(sourceTexts, srcLang, tgtLang);
    }

    @PostMapping("/api.json")
    public ResponseEntity<String> postWelcome(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestBody String json) throws IOException {
        Set<Map.Entry<String, List<String>>> headers = httpHeaders.entrySet();
        for (Map.Entry<String, List<String>> header : headers) {
            log.info("key : value = {} : {}", header.getKey(), header.getValue());
        }

        // 요청 헤더 생성
        HttpHeaders makeHeaders = new HttpHeaders();
        makeHeaders.set("accept", "*/*");
        makeHeaders.set("accept-encoding", "gzip, deflate");
        makeHeaders.set("accept-language", "ko,en-US;q=0.9,en;q=0.8,it-IT;q=0.7,it;q=0.6,ja;q=0.5");
        makeHeaders.set("cache-Control", "no-cache");
        makeHeaders.set("connection", "keep-alive");
        makeHeaders.set("content-type", "text/plain;charset=UTF-8");
        makeHeaders.set("host", "nmsg.nicovideo.jp");
        makeHeaders.set("origin", "http://www.nicovideo.jp");
        makeHeaders.set("pragma", "no-cache");
        makeHeaders.set("referer", "http://www.nicovideo.jp/watch/sm5361236");
        makeHeaders.set("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");

        HttpEntity<String> httpEntity = new HttpEntity<>(json, makeHeaders);

        // gzip으로 인해 아파치 라이브러리 사용 - HttpComponentsClientHttpRequestFactory
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        // content type 설정
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "text/json; charset=UTF-8");

        // json 얻어온다
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://pobi.god/api.json", httpEntity, String.class);
        log.info(responseEntity.getBody());

        //-------------------------------------------------------------------------------

        // json to Object
        List<Item> items = null;
        try {
            items = mapper.readValue(responseEntity.getBody(),
                    new TypeReference<List<Item>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO 일단 100개만 번역
        List<Pair> pairList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Optional.ofNullable(items.get(i).findContents()).ifPresent(pairList::add);
        }

        // 번역할 텍스트 추출
        List<String> toTransList = new ArrayList<>();
        for (Pair pair : pairList) {
            toTransList.add(pair.getMsg());
        }

        // 리스트 번역
        List<Translation> translatedList = translationsList(toTransList);

        // 번역 텍스트 put
        for (int i = 0; i < pairList.size(); i++) {
            pairList.get(i).getContents().put("content", translatedList.get(i).getTranslatedText());
        }

        // 테스트 번역 텍스트 출력
        for (Item item : items) {
            Optional.ofNullable(item.get("chat")).ifPresent(chat -> System.out.println(chat.get("content")));
        }

        //-------------------------------------------------------------------------------

        // object To Json
        String tranedJson = null;
        try {
            tranedJson = mapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 테스트 json 출력
        System.out.println(tranedJson);

        // 브라우저로 ResponseEntity 리턴
        return new ResponseEntity<>(tranedJson, responseHeaders, HttpStatus.OK);
    }

    private static class Contents extends HashMap<String, Object> {
        Pair findContent() {
            for (String s : keySet()) {
                if (s.equals("content")) {
                    String originalText = (String) get("content");

//                    Object obj = this;

//                    log.info("content : {}", originalText);
//                    Translation translation = translateTextWithOptions(originalText);
//                    put("content", translation.getTranslatedText());

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
                if (itemSet.getKey().equals("chat")) {
                    Contents contents = itemSet.getValue();
                    log.info("item find");
                    return contents.findContent();
                }
            }

            return null;
        }
    }
}
