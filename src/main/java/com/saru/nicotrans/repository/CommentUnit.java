package com.saru.nicotrans.repository;

import javax.persistence.*;
import java.util.Objects;

// TODO 코멘트 유닛 테스트 추가 해야
@Entity
public class CommentUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String referer;
    private int count;

    @Lob
    private String originalJson;

    @Lob
    private String transJson;

    public CommentUnit() {
    }

    public CommentUnit(Long id, String referer, int count, String originalJson, String transJson) {
        this.id = id;
        this.referer = referer;
        this.count = count;
        this.originalJson = originalJson;
        this.transJson = transJson;
    }

    public Long getId() {
        return id;
    }

    public CommentUnit setId(Long id) {
        this.id = id;
        return this;
    }

    public String getReferer() {
        return referer;
    }

    public CommentUnit setReferer(String referer) {
        this.referer = referer;
        return this;
    }

    public int getCount() {
        return count;
    }

    public CommentUnit setCount(int count) {
        this.count = count;
        return this;
    }

    public String getOriginalJson() {
        return originalJson;
    }

    public CommentUnit setOriginalJson(String originalJson) {
        this.originalJson = originalJson;
        return this;
    }

    public String getTransJson() {
        return transJson;
    }

    public CommentUnit setTransJson(String transJson) {
        this.transJson = transJson;
        return this;
    }

    public CommentUnit update(int count, String translatedComment) {
        this.count = count;
        this.transJson = translatedComment;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentUnit that = (CommentUnit) o;
        return count == that.count &&
                Objects.equals(referer, that.referer) &&
                Objects.equals(originalJson, that.originalJson) &&
                Objects.equals(transJson, that.transJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referer, count, originalJson, transJson);
    }
}
