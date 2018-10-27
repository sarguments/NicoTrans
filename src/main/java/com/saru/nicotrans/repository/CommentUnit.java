package com.saru.nicotrans.repository;

import javax.persistence.*;
import java.util.Objects;

// TODO 코멘트 유닛 테스트 추가 해야
// TODO Validation 추가
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

    public String getReferer() {
        return referer;
    }

    public int getCount() {
        return count;
    }

    public String getOriginalJson() {
        return originalJson;
    }

    public String getTransJson() {
        return transJson;
    }

    public CommentUnit update(int count, String translatedComment, String originalJson) {
        this.count = count;
        this.transJson = translatedComment;
        this.originalJson = originalJson;
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
