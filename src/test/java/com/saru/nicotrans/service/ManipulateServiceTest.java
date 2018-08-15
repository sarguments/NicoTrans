package com.saru.nicotrans.service;

import com.jayway.jsonpath.JsonPath;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManipulateServiceTest {
    private static final Logger log = LoggerFactory.getLogger(ManipulateServiceTest.class);

    @Mock
    private ManipulateService mockManService;

    @Resource(name = "manipulateService")
    private ManipulateService manipulateService;

    private String json;
    private String jsonResult;
    private List<String> contents;

    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);

        File jsonFile = ResourceUtils.getFile("classpath:static/testJson.json");
        File jsonResultFile = ResourceUtils.getFile("classpath:static/testJsonResult.json");

        // TODO Read File Content
        json = new String(Files.readAllBytes(jsonFile.toPath()));
        jsonResult = new String(Files.readAllBytes(jsonResultFile.toPath()));

        contents = JsonPath.parse(json)
                .read("$..chat.content", List.class);
    }

    @Test
    public void jsonToItems() throws IOException {
        List<Item> items = manipulateService.responseJsonToItems(json);
        log.debug("items : {}", items);
    }

    @Test
    public void extractPairs() {
        List<Item> items = manipulateService.responseJsonToItems(json);
        List<Pair> pairs = manipulateService.itemsToPairs(items);
        log.debug("pairs : {}", pairs);
    }

    @Test
    public void extractTexts() {
        List<Item> items = manipulateService.responseJsonToItems(json);
        List<Pair> pairs = manipulateService.itemsToPairs(items);
        List<String> texts = manipulateService.extractToTranslateTexts(pairs);
        log.debug("texts : {}", texts);

        assertThat(texts, is(contents));
    }

    @Test
    public void itemsToJson() {
        List<Item> items = manipulateService.responseJsonToItems(json);
        String transedJson = manipulateService.itemsToJson(items);
        log.debug("json : {}", transedJson);

        String transedJsonContent = JsonPath.parse(transedJson)
                .read("$..chat.content").toString();

        String jsonContent = JsonPath.parse(json)
                .read("$..chat.content").toString();

        assertThat(transedJsonContent, is(jsonContent));
    }

    @Test
    public void printTranslateText() {
        when(mockManService.itemsToJson(anyList()))
                .thenReturn(jsonResult);

        List<Item> items = manipulateService.responseJsonToItems(json);
        String transedJson = mockManService.itemsToJson(items);

        log.debug("transedJson : {}", transedJson);
    }
}
