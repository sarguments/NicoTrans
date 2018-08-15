package com.saru.nicotrans.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class NetworkService {
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);
    private static final String TEXT_JSON_CHARSET_UTF_8 = "text/json; charset=UTF-8";
    private static final String COMMENT_SERVER_URL = "http://pobi.god/api.json";

    // TODO 컨트롤러에 있는 로직 서비스로 옮겨야 함
    private RestTemplate gzipRestTemplate;

    @Autowired
    public NetworkService(RestTemplate gzipRestTemplate) {
        this.gzipRestTemplate = gzipRestTemplate;
    }

    public HttpHeaders makeHeaders(HttpHeaders httpHeaders) {
        HttpHeaders toRequestHeaders = new HttpHeaders();

        toRequestHeaders.setAccept(httpHeaders.getAccept());
        toRequestHeaders.set(HttpHeaders.ACCEPT_ENCODING,
                Objects.requireNonNull(httpHeaders.get(HttpHeaders.ACCEPT_ENCODING)).toString());
        toRequestHeaders.setAcceptLanguage(httpHeaders.getAcceptLanguage());
        toRequestHeaders.setCacheControl(httpHeaders.getCacheControl());
        toRequestHeaders.setConnection(httpHeaders.getConnection());
        toRequestHeaders.setContentType(httpHeaders.getContentType());
        toRequestHeaders.setHost(httpHeaders.getHost());
        toRequestHeaders.setOrigin(httpHeaders.getOrigin());
        toRequestHeaders.setPragma(httpHeaders.getPragma());
        toRequestHeaders.set(HttpHeaders.REFERER,
                Objects.requireNonNull(httpHeaders.get(HttpHeaders.REFERER)).toString());
        toRequestHeaders.set(HttpHeaders.USER_AGENT,
                Objects.requireNonNull(httpHeaders.get(HttpHeaders.USER_AGENT)).toString());

        return toRequestHeaders;
    }

    public HttpHeaders makeResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, TEXT_JSON_CHARSET_UTF_8);
        return responseHeaders;
    }

    public ResponseEntity<String> getResponseEntity(HttpEntity<String> httpEntity) {
        return gzipRestTemplate.postForEntity(COMMENT_SERVER_URL, httpEntity, String.class);
    }
}
