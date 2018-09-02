package com.saru.nicotrans.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.saru.nicotrans.type_and_config.ContentType.CHAT;

public class Item extends HashMap<String, Contents> {
    public Pair findContents() {
        Set<Entry<String, Contents>> itemSets = entrySet();
        for (Map.Entry<String, Contents> itemSet : itemSets) {
            if (itemSet.getKey().equals(CHAT.getName())) {
                Contents contents = itemSet.getValue();
                return contents.findContent();
            }
        }

        return null;
    }
}