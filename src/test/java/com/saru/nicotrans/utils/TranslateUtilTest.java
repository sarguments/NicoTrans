package com.saru.nicotrans.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.Translation;
import com.saru.nicotrans.JsonTestInit;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import com.saru.nicotrans.service.ManipulateService;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TranslateUtilTest extends JsonTestInit {
    private ManipulateService manipulateService = new ManipulateService(new ObjectMapper());

    @Test
    public void translate() {
        List<Item> items = manipulateService.responseJsonToItems(getJson());

        // 번역할 텍스트의 원본 Content 참조와 텍스트 pairs로 추출
        List<Pair> pairs = manipulateService.itemsToPairs(items);

        // pairs에서 번역할 텍스트만 따로 추출
        List<String> toTransTexts = manipulateService.extractToTranslateTexts(pairs);
        List<Translation> translates = manipulateService.translateTexts(getBeforeContents());
        manipulateService.putTranslatedTexts(pairs, translates);

        List<String> translatedTexts = manipulateService.extractToTranslateTexts(pairs);
        assertThat(translatedTexts, is(getAfterContents()));
    }
}
