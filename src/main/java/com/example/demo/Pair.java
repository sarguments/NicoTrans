package com.example.demo;

import java.util.Map;

public class Pair {
    private Map<String, Object> contents;
    private String msg;

    public Pair() {
    }

    public Pair(Map<String, Object> contents, String msg) {
        this.contents = contents;
        this.msg = msg;
    }

    public Map<String, Object> getContents() {
        return contents;
    }

    public void setContents(Map<String, Object> contents) {
        this.contents = contents;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
