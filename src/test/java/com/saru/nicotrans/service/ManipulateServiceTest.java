package com.saru.nicotrans.service;

import com.google.cloud.translate.Translation;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import com.saru.nicotrans.utils.LogUtil;
import com.saru.nicotrans.utils.TranslateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManipulateServiceTest {
    private static final Logger log = LoggerFactory.getLogger(ManipulateServiceTest.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Resource(name = "manipulateService")
    private ManipulateService manipulateService;

    // TODO 이거저거 문제가 많다
    private String json;

    @Before
    public void init() throws IOException {
        File file = ResourceUtils.getFile("classpath:static/testJson.json");

        // TODO Read File Content
        json = new String(Files.readAllBytes(file.toPath()));
//        log.debug("content : {}", json);
    }

    @Test
    public void jsonToItems() throws IOException {
        log.debug("{}", json);

        List<Item> items = manipulateService.responseJsonToItems(json);
        log.debug("items : {}", items);
    }

    @Test
    public void extractPairs() {
        List<Item> items = manipulateService.responseJsonToItems(json);
        manipulateService.itemsToPairs(items);
    }

    @Test
    public void extractTexts() {
        List<Item> items = manipulateService.responseJsonToItems(json);
        List<Pair> pairs = manipulateService.itemsToPairs(items);
        List<String> texts = manipulateService.extractToTranslateTexts(pairs);
        log.debug("texts : {}", texts);
    }

    @Test
    public void itemsToJson() {
        List<Item> items = manipulateService.responseJsonToItems(json);
        String transedJson = manipulateService.itemsToJson(items);
        log.debug("json : {}", transedJson);
    }

    @Test
    public void printTranslateText() {
        // json to Items
        List<Item> items = manipulateService.responseJsonToItems(json);

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
    }
}
