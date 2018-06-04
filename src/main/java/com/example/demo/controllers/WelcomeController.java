package com.example.demo.controllers;

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

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class WelcomeController {
    private static final Logger log = LoggerFactory.getLogger(WelcomeController.class);

    @GetMapping("")
    public String welcome() {
        log.info("WOW!");

        return "Hello";
    }

    // http://202.248.252.234/api.json/

    @PostMapping("/api.json")
    public ResponseEntity<String> postWelcome(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestBody String json) {
        Set<Map.Entry<String, List<String>>> headers = httpHeaders.entrySet();
        for (Map.Entry<String, List<String>> header : headers) {
            log.info("key : value = {} : {}", header.getKey(), header.getValue());
        }

        log.info(json);

        HttpHeaders makeHeaders = new HttpHeaders();
        makeHeaders.set("accept", "*/*");
        makeHeaders.set("accept-encoding", "gzip, deflate");
        makeHeaders.set("accept-language", "ko,en-US;q=0.9,en;q=0.8,it-IT;q=0.7,it;q=0.6,ja;q=0.5");
        makeHeaders.set("cache-Control", "no-cache");
        makeHeaders.set("connection", "keep-alive");
//        makeHeaders.set("content-Length", "848");
        makeHeaders.set("content-type", "text/plain;charset=UTF-8");
        makeHeaders.set("host", "nmsg.nicovideo.jp");
        makeHeaders.set("origin", "http://www.nicovideo.jp");
        makeHeaders.set("pragma", "no-cache");
        makeHeaders.set("referer", "http://www.nicovideo.jp/watch/sm5361236");
        makeHeaders.set("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");

        HttpEntity<String> httpEntity = new HttpEntity<>(json, makeHeaders);

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "text/json; charset=UTF-8");

//        Access-Control-Allow-Headers: Content-Type
//        Access-Control-Allow-Methods: POST,GET,OPTIONS,HEAD
//        Access-Control-Allow-Origin: *
//        Cache-Control: max-age=0
//        Connection: Keep-Alive
//        Content-Encoding: gzip
//        Content-Length: 13775
//        Content-Type: text/json; charset=UTF-8
//        Keep-Alive: timeout=15, max=100
//        Vary: Accept-Encoding

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://pobi.god/api.json", httpEntity, String.class);

        log.info(responseEntity.getBody());

        return new ResponseEntity<>(responseEntity.getBody(), responseHeaders, HttpStatus.OK);
    }
}
