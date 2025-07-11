package com.cemenghui.course.service;
import com.cemenghui.course.entity.Chapter;
import java.util.List;

public interface ChapterService {
    List<Chapter> getChaptersByCourseId(Long courseId);
} 