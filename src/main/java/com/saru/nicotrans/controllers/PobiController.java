package com.saru.nicotrans.controllers;

import com.google.cloud.translate.Translation;
import com.saru.nicotrans.Utils.LogUtil;
import com.saru.nicotrans.Utils.TranslateUtil;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import com.saru.nicotrans.service.ManipulateService;
import com.saru.nicotrans.service.NetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class PobiController {
    private static final String COMMENT_SERVER_URL = "http://pobi.god/api.json";


    private static final Logger log = LoggerFactory.getLogger(PobiController.class);

    @GetMapping("")
    public String welcome() {
        log.info("WOW!");

        return "Hello";
    }

    @PostMapping("/api.json")
    public ResponseEntity<String> postWelcome(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestBody String json) {
        ManipulateService manipulateService = new ManipulateService();
        NetworkService networkService = new NetworkService();

        Set<Map.Entry<String, List<String>>> headers = httpHeaders.entrySet();
        for (Map.Entry<String, List<String>> header: headers) {
            log.info("key : value = {} : {}", header.getKey(), header.getValue());
        }

        // 요청 헤더 생성 후 json과 같이 httpEntity 조합
        HttpEntity<String> httpEntity = new HttpEntity<>(json, networkService.makeHeaders(httpHeaders));

        // gzip으로 인해 아파치 라이브러리 사용 - HttpComponentsClientHttpRequestFactory
        RestTemplate restTemplate = networkService.makeRestTemplate();

        // json 얻어온다
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(COMMENT_SERVER_URL, httpEntity, String.class);
        log.info(responseEntity.getBody());

        //-------------------------------------------------------------------------------

        // json to Items
        List<Item> items = networkService.responseJsonToItems(responseEntity);

        // 번역할 텍스트의 원본 Content 참조와 텍스트 pairs로 추출
        List<Pair> pairs = manipulateService.itemsToPairs(items);
        log.info("pairList size : {}", pairs.size());

        // pairs에서 번역할 텍스트만 따로 추출
        List<String> toTransTexts = manipulateService.extractToTranslateTexts(pairs);
        log.info("toTransList size : {}", toTransTexts.size());

        // 추출한 리스트 번역
        List<Translation> translatedTexts = TranslateUtil.translateList(toTransTexts);
        log.info("translatedList size : {}", translatedTexts.size());

        // 번역 텍스트 다시 put
        manipulateService.putTranslatedTexts(pairs, translatedTexts);

        // 테스트 번역 텍스트 출력
        LogUtil.printTranslateText(items);

        //-------------------------------------------------------------------------------

        // object To Json
        String translatedJson = manipulateService.itemsToJson(items);

        // 테스트 json 출력
        log.info(translatedJson);

        // 응답 content type 설정 후 브라우저로 ResponseEntity 리턴
        return new ResponseEntity<>(translatedJson, networkService.makeResponseHeaders(), HttpStatus.OK);
    }


}
