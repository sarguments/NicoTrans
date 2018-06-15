package com.saru.nicotrans;

public enum ContentType {
    CHAT("chat"),
    CONTENT("content");

    private final String name;

    ContentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
