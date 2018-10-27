package com.saru.nicotrans.service;

import com.google.cloud.translate.Translation;
import com.saru.nicotrans.JsonTestInit;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import com.saru.nicotrans.repository.CommentUnit;
import com.saru.nicotrans.repository.CommentUnitBuilder;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        String resultJSon = manipulateService.translateResponseJson(getJson(), "test");
        assertTrue(resultJSon.contains("재료입니까"));
        assertTrue(resultJSon.contains("VHS에서 10 회 정도 더빙 반복듯한"));
        assertTrue(resultJSon.contains("초 화질 다. 잘못은 없지만 w"));
        log.debug("resultJson : {}", resultJSon);
    }

    @Test
    public void checkRepositoryTest() {
        CommentUnit commentUnit = new CommentUnitBuilder()
                .setId(1L)
                .setCount(1)
                .setOriginalJson("ori")
                .setReferer("http://where.com")
                .setTransJson("trans").createCommentUnit();

        ManipulateService mockManipulateService = mock(ManipulateService.class);
        when(mockManipulateService.findComment("http://where.com")).thenReturn(commentUnit);
        assertThat(mockManipulateService.findComment("http://where.com").getTransJson(), is("trans"));
    }

    @Test
    public void saveRepositoryTest() {
        CommentUnit commentUnit = new CommentUnitBuilder()
                .setId(1L)
                .setCount(1)
                .setOriginalJson("ori")
                .setReferer("http://where.com")
                .setTransJson("trans").createCommentUnit();

        ManipulateService mockManipulateService = mock(ManipulateService.class);
        when(mockManipulateService.saveComment(commentUnit)).thenReturn(commentUnit);
        CommentUnit returnedComment = mockManipulateService.saveComment(commentUnit);
        assertThat(returnedComment, is(commentUnit));
    }

    @Test
    public void updateRepositoryTest() {
        CommentUnit commentUnit = new CommentUnitBuilder()
                .setId(1L)
                .setCount(1)
                .setOriginalJson("ori")
                .setReferer("http://where.com")
                .setTransJson("trans").createCommentUnit();

        CommentUnit returnedComment = manipulateService.saveComment(commentUnit);
        assertThat(returnedComment, is(commentUnit));

        returnedComment = manipulateService.updateComment("http://where.com", 10, "new trans", "ori");
        assertThat(returnedComment.getCount(), is(10));

        returnedComment = manipulateService.findComment("http://where.com");
        assertThat(returnedComment.getCount(), is(10));
    }
}