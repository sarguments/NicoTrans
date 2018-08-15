package com.saru.nicotrans.entity;

import java.util.HashMap;

import static com.saru.nicotrans.typeAndConfig.ContentType.CONTENT;

public class Contents extends HashMap<String, Object> {
    Pair findContent() {
        for (String s : keySet()) {
            if (s.equals(CONTENT.getName())) {
                String originalText = (String) get(CONTENT.getName());
                return new Pair(this, originalText);
            }
        }

        return null;
    }
}
