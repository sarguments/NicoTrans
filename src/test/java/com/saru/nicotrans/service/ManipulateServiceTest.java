package com.saru.nicotrans.service;

import com.google.cloud.translate.Translation;
import com.saru.nicotrans.JsonTestInit;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManipulateServiceTest extends JsonTestInit {
    private static final Logger log = LoggerFactory.getLogger(ManipulateServiceTest.class);

    @Resource(name = "manipulateService")
    private ManipulateService manipulateService;

    @Test
    public void jsonToItems() {
        List<Item> items = manipulateService.responseJsonToItems(getJson());
        log.debug("items : {}", items);
    }

    @Test
    public void itemsToPairs() {
        List<Item> items = manipulateService.responseJsonToItems(getJson());
        List<Pair> pairs = manipulateService.itemsToPairs(items);
        log.debug("pairs : {}", pairs);
    }

    @Test
    public void extractToTranslateTexts() {
        List<Item> items = manipulateService.responseJsonToItems(getJson());
        List<Pair> pairs = manipulateService.itemsToPairs(items);
        List<String> toTranslateTexts = manipulateService.extractToTranslateTexts(pairs);
        assertThat(toTranslateTexts, is(getBeforeContents()));
    }

    @Test
    public void putTranslatedTexts() {
        List<Item> items = manipulateService.responseJsonToItems(getJson());
        List<Pair> pairs = manipulateService.itemsToPairs(items);
        List<String> toTranslateTexts = manipulateService.extractToTranslateTexts(pairs);
        List<Translation> translates = manipulateService.translateTexts(toTranslateTexts);
        manipulateService.putTranslatedTexts(pairs, translates);

        // 페어 리스트에서 컨텐츠 객체 안에있는 컨텐트 스트링을 전부 꺼내서 리스트로 만든다.
        List<String> testContents = pairs.stream().map(Pair::getContentString)
                .collect(Collectors.toList());
        assertThat(testContents, is(getAfterContents()));
    }

    @Test
    public void translateResponseJson() {
        String resultJSon = manipulateService.translateResponseJson(getJson());
        assertTrue(resultJSon.contains("재료입니까"));
        assertTrue(resultJSon.contains("VHS에서 10 회 정도 더빙 반복듯한"));
        assertTrue(resultJSon.contains("초 화질 다. 잘못은 없지만 w"));
        log.debug("resultJson : {}", resultJSon);
    }
}
