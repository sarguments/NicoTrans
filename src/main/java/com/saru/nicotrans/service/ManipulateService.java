package com.saru.nicotrans.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.Translation;
import com.saru.nicotrans.entity.Contents;
import com.saru.nicotrans.entity.Item;
import com.saru.nicotrans.entity.Pair;
import com.saru.nicotrans.repository.CommentUnit;
import com.saru.nicotrans.repository.CommentUnitBuilder;
import com.saru.nicotrans.repository.CommentUnitRepository;
import com.saru.nicotrans.utils.TranslateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.saru.nicotrans.typeAndConfig.ContentType.CONTENT;

@Transactional
@Service
public class ManipulateService {
    private static final Logger log = LoggerFactory.getLogger(ManipulateService.class);
    private final ObjectMapper mapper;
    private CommentUnitRepository commentUnitRepository;

    @Autowired
    public ManipulateService(ObjectMapper mapper, CommentUnitRepository commentUnitRepository) {
        this.mapper = mapper;
        this.commentUnitRepository = commentUnitRepository;
    }

    public List<Pair> itemsToPairs(List<Item> items) {
        List<Pair> pairList = new ArrayList<>();
        for (Item item : items) {
            Optional.ofNullable(item.findContents()).ifPresent(pairList::add);
        }

        return pairList;
    }

    public String itemsToJson(List<Item> items) {
        String translatedJson = null;

        try {
            translatedJson = mapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            log.debug(e.getMessage());
        }
        return translatedJson;
    }

    public List<String> extractToTranslateTexts(List<Pair> pairList) {
        List<String> toTransList = new ArrayList<>();
        for (Pair pair : pairList) {
            toTransList.add(pair.getContentString());
        }

        return toTransList;
    }

    public List<Translation> translateTexts(List<String> toTransTexts) {
        // 추출한 리스트 번역
        return TranslateUtil.translateList(toTransTexts);
    }

    public void putTranslatedTexts(List<Pair> pairs, List<Translation> translatedTexts) {
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            Contents contents = pair.getContents();
            Translation translation = translatedTexts.get(i);
            contents.put(CONTENT.getName(), translation.getTranslatedText());
        }
    }

    public List<Item> responseJsonToItems(String body) {
        List<Item> items = null;

        try {
            items = mapper.readValue(body,
                    new TypeReference<List<Item>>() {
                    });
        } catch (IOException e) {
            log.debug(e.getMessage());
        }

        return items;
    }

    public String translateResponseJson(String responseJson, String referer) {
        List<Item> items = responseJsonToItems(responseJson);

        // 번역할 텍스트의 원본 Content 참조와 텍스트 pairs로 추출
        List<Pair> pairs = itemsToPairs(items);

        // 기존에 받은적이 있는가?
        // referer에 해당하는 CommentUnit 객체를 찾는다
        CommentUnit commentUnit = findComment(referer);

        // 없으면 번역
        if (commentUnit == null) {
            return saveCommentProcess(referer, items, pairs);
        }

        // 갯수 체크
        int itemCount = items.size();
        int prevCount = commentUnit.getCount();

        // 기존과 다르면 번역한 후 갯수랑 같이 저장 후 json 리턴
        if (prevCount != itemCount) {
            String translatedJson = translateProcess(items, pairs);
            updateComment(referer, itemCount, translatedJson);
            return translatedJson;
        }

        // 기존과 같으면 기존 json 리턴
        return commentUnit.getTransJson();
    }

    private String saveCommentProcess(String referer, List<Item> items, List<Pair> pairs) {
        String translatedJson;// pairs에서 번역할 텍스트만 따로 추출
        translatedJson = translateProcess(items, pairs);

        // TODO 갯수랑 같이 저장
        CommentUnit toSaveComment = new CommentUnitBuilder().setCount(items.size())
                .setReferer(referer)
                .setTransJson(translatedJson).createCommentUnit();
        saveComment(toSaveComment);
        return translatedJson;
    }

    private String translateProcess(List<Item> items, List<Pair> pairs) {
        String translatedJson;
        List<String> toTransTexts = extractToTranslateTexts(pairs);

        // 번역후 텍스트 다시 put
        List<Translation> translates = translateTexts(toTransTexts);
        putTranslatedTexts(pairs, translates);

        // json 리턴
        translatedJson = itemsToJson(items);
        return translatedJson;
    }

    public CommentUnit findComment(String referer) {
        return commentUnitRepository.findByReferer(referer);
    }

    public CommentUnit saveComment(CommentUnit commentUnit) {
        return commentUnitRepository.save(commentUnit);
    }

    public CommentUnit updateComment(String referer, int count, String translatedComment) {
        CommentUnit commentUnit = findComment(referer);
        return commentUnit.update(count, translatedComment);
    }
}
