package com.saru.nicotrans.entity;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PairTest {
    @Test
    public void create() {
        Contents contents = new Contents();
        contents.put("content", "test content");

        Pair pair = new Pair(contents, "msg");
        assertThat(pair.getContentString(), is("test content"));
    }
}
