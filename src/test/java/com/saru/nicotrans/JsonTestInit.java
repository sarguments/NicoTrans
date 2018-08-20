package com.saru.nicotrans;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class JsonTestInit {
    private String json;
    private String jsonResult;
    private List<String> beforeContents;
    private List<String> afterContents;

    public String getJson() {
        return json;
    }

    public String getJsonResult() {
        return jsonResult;
    }

    public List<String> getBeforeContents() {
        return beforeContents;
    }

    public List<String> getAfterContents() {
        return afterContents;
    }

    @Before
    public void init() throws IOException {
        File jsonFile = ResourceUtils.getFile("classpath:static/testJson.json");
        File jsonResultFile = ResourceUtils.getFile("classpath:static/testJsonResult.json");

        // TODO Read File Content
        json = new String(Files.readAllBytes(jsonFile.toPath()));
        jsonResult = new String(Files.readAllBytes(jsonResultFile.toPath()));

        beforeContents = JsonPath.parse(json)
                .read("$..chat.content", List.class);

        afterContents = JsonPath.parse(jsonResult)
                .read("$..chat.content", List.class);
    }
}
