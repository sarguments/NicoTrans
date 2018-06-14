package com.example.demo;

import java.util.HashMap;

public class Pair {
    private HashMap<String, Object> contents;
    private String msg;

    public Pair() {
    }

    public Pair(HashMap<String, Object> contents, String msg) {
        this.contents = contents;
        this.msg = msg;
    }

    public HashMap<String, Object> getContents() {
        return contents;
    }

    public void setContents(HashMap<String, Object> contents) {
        this.contents = contents;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
