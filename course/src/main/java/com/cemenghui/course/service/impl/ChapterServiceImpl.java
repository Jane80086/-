package com.cemenghui.course.service.impl;
import com.cemenghui.course.dao.ChapterDao;
import com.cemenghui.course.entity.Chapter;
import com.cemenghui.course.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChapterServiceImpl implements ChapterService {
    @Autowired
    private ChapterDao chapterDao;
    @Override
    public List<Chapter> getChaptersByCourseId(Long courseId) {
        List<Chapter> chapters = chapterDao.findByCourseId(courseId);
        return chapters == null ? java.util.Collections.emptyList() : chapters;
    }
} 