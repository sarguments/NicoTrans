package com.saru.nicotrans.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saru.nicotrans.entity.Item;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class NetworkService {
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);
    private static final String TEXT_JSON_CHARSET_UTF_8 = "text/json; charset=UTF-8";
    private final ObjectMapper mapper = new ObjectMapper();

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

    public RestTemplate makeRestTemplate() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        return new RestTemplate(clientHttpRequestFactory);
    }

    public List<Item> responseJsonToItems(ResponseEntity<String> responseEntity) {
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

    public HttpHeaders makeResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, TEXT_JSON_CHARSET_UTF_8);
        return responseHeaders;
    }
}
