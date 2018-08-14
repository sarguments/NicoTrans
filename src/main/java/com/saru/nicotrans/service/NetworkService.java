package com.saru.nicotrans.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class NetworkService {
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);
    private static final String TEXT_JSON_CHARSET_UTF_8 = "text/json; charset=UTF-8";
    private ObjectMapper mapper;
    private RestTemplate restTemplate;

    // TODO 컨트롤러에 있는 로직 서비스로 옮겨야 함

    @Autowired
    public NetworkService(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
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

    // TODO Bean으로
    public RestTemplate makeRestTemplate() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        return new RestTemplate(clientHttpRequestFactory);
    }

//    public List<Item> responseJsonToItems(String response) {
//        List<Item> items = null;
//
//        try {
//            items = mapper.readValue(response,
//                    new TypeReference<List<Item>>() {
//                    });
//        } catch (IOException e) {
//            log.info(e.getMessage());
//        }
//        return items;
//    }

    public HttpHeaders makeResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, TEXT_JSON_CHARSET_UTF_8);
        return responseHeaders;
    }
}
