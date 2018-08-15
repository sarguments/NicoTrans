package com.saru.nicotrans.entity;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContentsTest {
    @Test
    public void create() {
        Contents contents = new Contents();
        contents.put("content", "test content");
        assertThat(contents.getContentString(), is("test content"));

        Pair pair = contents.findContent();
        assertThat(pair.getContentString(), is("test content"));
    }
}
