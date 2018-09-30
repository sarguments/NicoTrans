package com.saru.nicotrans.entity;

import java.util.HashMap;

import static com.saru.nicotrans.typeAndConfig.ContentType.CONTENT;

public class Contents extends HashMap<String, Object> {
    Pair findContent() {
//        return keySet().stream()
//                .filter(z -> s.equals(CONTENT.getName()))
//                .map(c -> new Pair(this, c)).findFirst().orElse(null);

        Object originalObject = get(CONTENT.getName());
        if (originalObject == null) {
            return null;
        }

        return new Pair(this, (String) originalObject);

//        for (String s: keySet()) {
//            if (s.equals(CONTENT.getName())) {
//                String originalText = (String) get(CONTENT.getName());
//                return new Pair(this, originalText);
//            }
//        }
//
//        return null;
    }

    String getContentString() {
        return (String) get(CONTENT.getName());
    }
}
