package com.saru.nicotrans.controllers;

import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import com.saru.nicotrans.service.ManipulateService;
import com.saru.nicotrans.service.NetworkService;
import com.saru.nicotrans.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class PobiController {
    private static final Logger log = LoggerFactory.getLogger(PobiController.class);

    @Resource(name = "manipulateService")
    private ManipulateService manipulateService;

    @Resource(name = "networkService")
    private NetworkService networkService;

    @GetMapping("")
    public String welcome() {
        return "Now Running...";
    }

    @PostMapping("/api.json")
    public ResponseEntity<String> apiJson(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestBody String request) {
        // 헤더 로그 출력
        LogUtil.printRequestHeahder(httpHeaders);

        // 요청 헤더 생성 후 json과 같이 httpEntity 조합
        HttpEntity<String> httpEntity = new HttpEntity<>(request, networkService.makeHeaders(httpHeaders));

        // json 얻어오고 json to Items
        List<Item> items = manipulateService.responseJsonToItems(networkService.getResponseEntity(httpEntity));

        // 번역할 텍스트의 원본 Content 참조와 텍스트 pairs로 추출
        List<Pair> pairs = manipulateService.itemsToPairs(items);

        // pairs에서 번역할 텍스트만 따로 추출
        List<String> toTransTexts = manipulateService.extractToTranslateTexts(pairs);

        // 번역 텍스트 다시 put
        manipulateService.getTranslatedTexts(pairs, toTransTexts);

        // 응답 content type 설정 후 브라우저로 ResponseEntity 리턴
        return new ResponseEntity<>(manipulateService.itemsToJson(items),
                networkService.makeResponseHeaders(), HttpStatus.OK);
    }
}
