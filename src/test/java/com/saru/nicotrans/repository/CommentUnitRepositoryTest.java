package com.saru.nicotrans.repository;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.UnexpectedTypeException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class CommentUnitRepositoryTest {
    @Mock
    CommentUnitRepository commentUnitRepository;

    @Test
    public void save() {
        MockitoAnnotations.initMocks(this);
        assertThat(commentUnitRepository).isNotNull();
        CommentUnit commentUnit = new CommentUnitBuilder()
                .setId(1L)
                .setCount(1)
                .setOriginalJson("ori")
                .setReferer("http://where.com")
                .setTransJson("trans").createCommentUnit();

        when(commentUnitRepository.findById(1L)).thenReturn(Optional.ofNullable(commentUnit));
        CommentUnit returnUnit = commentUnitRepository.findById(1L).orElseThrow(UnexpectedTypeException::new);

        assertThat(returnUnit.getTransJson()).isEqualTo("trans");
    }
}
