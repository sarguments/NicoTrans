package com.saru.nicotrans.entity;

import java.util.HashMap;

import static com.saru.nicotrans.typeAndConfig.ContentType.CONTENT;

public class Contents extends HashMap<String, Object> {
    Pair findContent() {
        // 키셋을 돌면서 "CONTENT" 를 찾은다음, "CONTENT" 의 레퍼런스와 코멘트 텍스트 리턴
        return keySet().stream()
                .filter(s -> s.equals(CONTENT.getName()))
                .map(c -> new Pair(this, c)).findFirst().orElse(null);
    }

    String getContentString() {
        return (String) get(CONTENT.getName());
    }
}
