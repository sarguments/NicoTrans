package com.saru.nicotrans.controllers;

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
    public ResponseEntity<String> translateComments(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestBody String request) {
        // 헤더 로그 출력
        LogUtil.printRequestHeahder(httpHeaders);

        // 레퍼러를 따로 보관해 놓는다.
        String referer = httpHeaders.getFirst(HttpHeaders.REFERER);

        // 요청 헤더 생성 후 json과 같이 httpEntity 조합
        HttpEntity<String> httpEntity = new HttpEntity<>(request, networkService.makeHeaders(httpHeaders));

        // json 얻어오고 json to Items
        String responseJson = networkService.getResponseJson(httpEntity);
        String translatedJson = manipulateService.translateResponseJson(responseJson, referer);

        // 응답 content type 설정 후 브라우저로 ResponseEntity 리턴
        return new ResponseEntity<>(translatedJson,
                networkService.makeResponseHeaders(), HttpStatus.OK);
    }
}
