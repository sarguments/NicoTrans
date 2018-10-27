package com.saru.nicotrans.repository;

public class CommentUnitBuilder {
    private Long id;
    private String referer;
    private int count;
    private String originalJson;
    private String transJson;

    public CommentUnitBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public CommentUnitBuilder setReferer(String referer) {
        this.referer = referer;
        return this;
    }

    public CommentUnitBuilder setCount(int count) {
        this.count = count;
        return this;
    }

    public CommentUnitBuilder setOriginalJson(String originalJson) {
        this.originalJson = originalJson;
        return this;
    }

    public CommentUnitBuilder setTransJson(String transJson) {
        this.transJson = transJson;
        return this;
    }

    public CommentUnit createCommentUnit() {
        return new CommentUnit(id, referer, count, originalJson, transJson);
    }
}