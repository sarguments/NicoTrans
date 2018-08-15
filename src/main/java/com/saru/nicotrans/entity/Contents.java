package com.saru.nicotrans.entity;

import java.util.HashMap;

import static com.saru.nicotrans.typeAndConfig.ContentType.CONTENT;

public class Contents extends HashMap<String, Object> {
    Pair findContent() {
        for (String s : keySet()) {
            if (s.equals(CONTENT.getName())) {
                return new Pair(this, getContentString());
            }
        }

        return null;
    }

    String getContentString() {
        return (String) get(CONTENT.getName());
    }
}
