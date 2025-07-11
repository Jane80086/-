package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.ChapterDao;
import com.cemenghui.course.entity.Chapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChapterServiceImplTest {
    @InjectMocks
    private ChapterServiceImpl chapterServiceImpl;
    @Mock
    private ChapterDao chapterDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChaptersByCourseId() {
        List<Chapter> chapters = Collections.singletonList(new Chapter());
        when(chapterDao.findByCourseId(1L)).thenReturn(chapters);
        List<Chapter> result = chapterServiceImpl.getChaptersByCourseId(1L);
        assertEquals(1, result.size());
    }
} 