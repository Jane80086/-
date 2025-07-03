package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.CourseHistoryService;
import com.cemenghui.course.service.impl.CourseManagerServiceImpl;
import com.cemenghui.course.service.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseManagerServiceImpl courseManagerService;

    @Autowired
    private CourseHistoryService courseHistoryService;

    /**
     * 创建新课程
     */
    @PostMapping("/create")
    public ResponseEntity<Result> createCourse(@RequestBody @Valid Course course) {
        try {
            Course createdCourse = courseManagerService.createCourse(course);
            return ResponseEntity.ok(Result.success("课程创建成功", createdCourse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("课程创建失败: " + e.getMessage()));
        }
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result> getCourseDetail(@PathVariable Long id) {
        try {
            Course course = courseService.getCourseDetail(id);
            return ResponseEntity.ok(Result.success(course));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.fail("课程不存在: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("获取课程详情失败: " + e.getMessage()));
        }
    }

    /**
     * 更新课程信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result> updateCourse(@PathVariable Long id, @RequestBody @Valid Course updatedCourse) {
        try {
            Course course = courseManagerService.editCourse(id, updatedCourse);
            return ResponseEntity.ok(Result.success("课程更新成功", course));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.fail("课程不存在: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("课程更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteCourse(@PathVariable Long id) {
        try {
            boolean success = courseManagerService.deleteCourse(id);
            if (success) {
                return ResponseEntity.ok(Result.success("课程删除成功", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Result.fail("课程不存在或删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("课程删除失败: " + e.getMessage()));
        }
    }

    /**
     * 获取课程列表
     */
    @GetMapping("/list")
    public ResponseEntity<Result> getCourseList() {
        try {
            // 直接返回模拟数据，避免数据库连接问题
            List<Course> mockCourses = createMockCourses();
            return ResponseEntity.ok(Result.success("使用模拟数据", mockCourses));
        } catch (Exception e) {
            e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Result.fail("获取课程列表失败: " + e.getMessage()));
        }
    }

    /**
     * 创建模拟课程数据
     */
    private List<Course> createMockCourses() {
        List<Course> courses = new ArrayList<>();
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Java基础教程");
        course1.setDescription("Java编程基础入门课程，适合零基础学习者");
        course1.setCoverImage("https://via.placeholder.com/300x200");
        course1.setVideoUrl("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4");
        course1.setDuration(120);
        course1.setPrice(new BigDecimal("0.0"));
        course1.setInstructorId(1L);
        course1.setCategory("编程开发");
        course1.setStatus("已发布");
        course1.setLikeCount(1250);
        course1.setFavoriteCount(890);
        courses.add(course1);

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Spring Boot实战");
        course2.setDescription("Spring Boot框架开发实战课程");
        course2.setCoverImage("https://via.placeholder.com/300x200");
        course2.setVideoUrl("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4");
        course2.setDuration(180);
        course2.setPrice(new BigDecimal("99.0"));
        course2.setInstructorId(2L);
        course2.setCategory("框架开发");
        course2.setStatus("已发布");
        course2.setLikeCount(890);
        course2.setFavoriteCount(650);
        courses.add(course2);

        Course course3 = new Course();
        course3.setId(3L);
        course3.setTitle("Vue.js前端开发");
        course3.setDescription("Vue.js前端框架开发教程");
        course3.setCoverImage("https://via.placeholder.com/300x200");
        course3.setVideoUrl("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4");
        course3.setDuration(150);
        course3.setPrice(new BigDecimal("79.0"));
        course3.setInstructorId(3L);
        course3.setCategory("前端开发");
        course3.setStatus("已发布");
        course3.setLikeCount(1100);
        course3.setFavoriteCount(750);
        courses.add(course3);
        return courses;
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    public ResponseEntity<Result> searchCourses(@RequestParam String keyword) {
        try {
            List<Course> courses = courseService.searchCourses(keyword);
            return ResponseEntity.ok(Result.success(courses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("搜索课程失败: " + e.getMessage()));
        }
    }

    /**
     * 提交课程审核
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<Result> submitForReview(@PathVariable Long id) {
        try {
            boolean success = courseManagerService.submitForReview(id);
            if (success) {
                return ResponseEntity.ok(Result.success("课程提交审核成功", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Result.fail("课程提交审核失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("提交审核失败: " + e.getMessage()));
        }
    }

    /**
     * 获取热门搜索趋势
     */
    @GetMapping("/trends")
    public ResponseEntity<Result> getHotSearchTrends() {
        try {
            List<String> trends = courseService.getHotSearchTrends();
            return ResponseEntity.ok(Result.success(trends));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("获取热门趋势失败: " + e.getMessage()));
        }
    }

    /**
     * 获取学习进度
     */
    @GetMapping("/{id}/progress")
    public Result getLearningProgress(@PathVariable Long id) {
        try {
            // 模拟获取学习进度数据
            Map<String, Object> progress = new HashMap<>();
            progress.put("progress", 35.5); // 学习进度百分比
            progress.put("currentTime", 1200); // 当前播放时间（秒）
            progress.put("totalTime", 3600); // 总时长（秒）
            progress.put("lastStudyTime", "2024-07-02 15:30:00");
            
            return Result.success("获取学习进度成功", progress);
        } catch (Exception e) {
            return Result.fail("获取学习进度失败: " + e.getMessage());
        }
    }

    /**
     * 保存学习进度
     */
    @PostMapping("/{id}/progress")
    public Result saveLearningProgress(@PathVariable Long id, @RequestBody Map<String, Object> progressData) {
        try {
            // 这里应该保存到数据库
            Double currentTime = (Double) progressData.get("currentTime");
            Double progress = (Double) progressData.get("progress");
            
            // 记录学习历史
            courseHistoryService.recordHistory(1L, id); // 假设用户ID为1
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "进度保存成功");
            result.put("currentTime", currentTime);
            result.put("progress", progress);
            
            return Result.success("保存学习进度成功", result);
        } catch (Exception e) {
            return Result.fail("保存学习进度失败: " + e.getMessage());
        }
    }

    /**
     * 记录播放行为
     */
    @PostMapping("/{id}/record")
    public Result recordPlayAction(@PathVariable Long id, @RequestBody Map<String, Object> actionData) {
        try {
            String action = (String) actionData.get("action");
            Double currentTime = (Double) actionData.get("currentTime");
            Long timestamp = (Long) actionData.get("timestamp");
            
            // 这里应该记录到数据库
            Map<String, Object> result = new HashMap<>();
            result.put("action", action);
            result.put("currentTime", currentTime);
            result.put("timestamp", timestamp);
            result.put("courseId", id);
            
            return Result.success("记录播放行为成功", result);
        } catch (Exception e) {
            return Result.fail("记录播放行为失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程笔记
     */
    @GetMapping("/{id}/notes")
    public Result getCourseNotes(@PathVariable Long id) {
        try {
            // 模拟笔记数据
            Map<String, Object>[] notes = new Map[2];
            
            Map<String, Object> note1 = new HashMap<>();
            note1.put("id", 1);
            note1.put("timestamp", 300);
            note1.put("content", "这里介绍了Vue.js的基础概念，需要重点理解响应式数据绑定。");
            note1.put("createTime", "2024-07-02 15:30:00");
            
            Map<String, Object> note2 = new HashMap<>();
            note2.put("id", 2);
            note2.put("timestamp", 1200);
            note2.put("content", "组件化开发是Vue.js的核心特性，要注意组件的生命周期。");
            note2.put("createTime", "2024-07-02 16:00:00");
            
            notes[0] = note1;
            notes[1] = note2;
            
            return Result.success("获取笔记成功", notes);
        } catch (Exception e) {
            return Result.fail("获取笔记失败: " + e.getMessage());
        }
    }

    /**
     * 添加笔记
     */
    @PostMapping("/{id}/notes")
    public Result addNote(@PathVariable Long id, @RequestBody Map<String, Object> noteData) {
        try {
            Integer timestamp = (Integer) noteData.get("timestamp");
            String content = (String) noteData.get("content");
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", System.currentTimeMillis()); // 模拟ID
            result.put("timestamp", timestamp);
            result.put("content", content);
            result.put("createTime", "2024-07-02 16:30:00");
            
            return Result.success("添加笔记成功", result);
        } catch (Exception e) {
            return Result.fail("添加笔记失败: " + e.getMessage());
        }
    }

    /**
     * 更新笔记
     */
    @PutMapping("/{id}/notes/{noteId}")
    public Result updateNote(@PathVariable Long id, @PathVariable Long noteId, @RequestBody Map<String, Object> noteData) {
        try {
            Integer timestamp = (Integer) noteData.get("timestamp");
            String content = (String) noteData.get("content");
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", noteId);
            result.put("timestamp", timestamp);
            result.put("content", content);
            result.put("updateTime", "2024-07-02 16:35:00");
            
            return Result.success("更新笔记成功", result);
        } catch (Exception e) {
            return Result.fail("更新笔记失败: " + e.getMessage());
        }
    }

    /**
     * 删除笔记
     */
    @DeleteMapping("/{id}/notes/{noteId}")
    public Result deleteNote(@PathVariable Long id, @PathVariable Long noteId) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "笔记删除成功");
            result.put("noteId", noteId);
            
            return Result.success("删除笔记成功", result);
        } catch (Exception e) {
            return Result.fail("删除笔记失败: " + e.getMessage());
        }
    }

    /**
     * 获取相关课程
     */
    @GetMapping("/{id}/related")
    public Result getRelatedCourses(@PathVariable Long id) {
        try {
            // 模拟相关课程数据
            Map<String, Object>[] relatedCourses = new Map[3];
            
            Map<String, Object> course1 = new HashMap<>();
            course1.put("id", 2);
            course1.put("title", "Vue.js 进阶教程");
            course1.put("instructorName", "张老师");
            course1.put("coverImage", "https://via.placeholder.com/120x80");
            course1.put("duration", 7200);
            
            Map<String, Object> course2 = new HashMap<>();
            course2.put("id", 3);
            course2.put("title", "React 实战开发");
            course2.put("instructorName", "李老师");
            course2.put("coverImage", "https://via.placeholder.com/120x80");
            course2.put("duration", 5400);
            
            Map<String, Object> course3 = new HashMap<>();
            course3.put("id", 4);
            course3.put("title", "前端工程化实践");
            course3.put("instructorName", "王老师");
            course3.put("coverImage", "https://via.placeholder.com/120x80");
            course3.put("duration", 6000);
            
            relatedCourses[0] = course1;
            relatedCourses[1] = course2;
            relatedCourses[2] = course3;
            
            return Result.success("获取相关课程成功", relatedCourses);
        } catch (Exception e) {
            return Result.fail("获取相关课程失败: " + e.getMessage());
        }
    }

    /**
     * 获取我的课程
     */
    @GetMapping("/my")
    public ResponseEntity<Result> getMyCourses() {
        try {
            // 这里应该根据当前登录用户获取课程
            // 暂时返回模拟数据
            List<Course> myCourses = createMockCourses().subList(0, 2);
            return ResponseEntity.ok(Result.success("获取我的课程成功", myCourses));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("获取我的课程失败: " + e.getMessage()));
        }
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail("参数校验失败: " + errorMessage));
    }

    /**
     * 下架课程（企业用户）
     */
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<Result> unpublishCourse(@PathVariable Long id) {
        try {
            boolean success = courseManagerService.unpublishCourse(id);
            if (success) {
                return ResponseEntity.ok(Result.success("课程已下架", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Result.fail("课程不存在或下架失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("课程下架失败: " + e.getMessage()));
        }
    }
} 