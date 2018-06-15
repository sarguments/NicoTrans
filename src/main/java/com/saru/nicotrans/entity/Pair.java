package com.saru.nicotrans.entity;

public class Pair {
    private Contents contents;
    private String msg;

    public Pair() {
    }

    Pair(Contents contents, String msg) {
        this.contents = contents;
        this.msg = msg;
    }

    public Contents getContents() {
        return contents;
    }

    public void setContents(Contents contents) {
        this.contents = contents;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
