package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.CourseHistoryService;
import com.cemenghui.course.service.CourseOptimizationService;
import com.cemenghui.course.service.impl.CourseManagerServiceImpl;
import com.cemenghui.course.service.NotFoundException;
import com.cemenghui.course.dao.CourseReviewHistoryDao;
import com.cemenghui.course.entity.CourseReviewHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.math.BigDecimal;
import com.cemenghui.course.service.impl.MinioServiceImpl;
import com.cemenghui.course.service.ChapterService;
import com.cemenghui.common.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.QnAService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseManagerServiceImpl courseManagerService;

    @Autowired
    private CourseHistoryService courseHistoryService;

    @Autowired
    private CourseReviewHistoryDao reviewHistoryDao;
    
    @Autowired
    private CourseOptimizationService courseOptimizationService;

    @Autowired
    private MinioServiceImpl minioServiceImpl;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private QnAService qnaService;

    /**
     * 创建新课程
     * @param course 课程对象
     * @param useAiOptimization 是否使用AI优化（可选，默认true）
     */
    @PostMapping("/create")
    public ResponseEntity<Result> createCourse(@RequestBody @Valid Course course,
                                               @RequestParam(value = "useAiOptimization", required = false, defaultValue = "true") boolean useAiOptimization) {
        try {
            // 校验视频/封面URL是否存在于MinIO（可选）
            boolean videoExists = true;
            boolean coverExists = true;
            String videoUrl = course.getVideoUrl();
            String imageUrl = course.getImageUrl();
            // 仅校验非空URL
            if (videoUrl != null && !videoUrl.isEmpty()) {
                videoExists = minioServiceImpl.fileExists(videoUrl.replaceFirst("^.*/course-files/", ""));
            }
            if (imageUrl != null && !imageUrl.isEmpty()) {
                coverExists = minioServiceImpl.fileExists(imageUrl.replaceFirst("^.*/course-files/", ""));
            }
            if (!videoExists) {
                return ResponseEntity.badRequest().body(Result.fail("视频文件在MinIO中不存在，请先上传！"));
            }
            if (!coverExists) {
                return ResponseEntity.badRequest().body(Result.fail("封面图片在MinIO中不存在，请先上传！"));
            }
            // AI优化课程标题和简介（可选，失败不影响创建）
            // if (useAiOptimization && course.getTitle() != null && course.getDescription() != null) {
            //     try {
            //         Map<String, String> optimizedInfo = courseOptimizationService.optimizeCourseInfo(
            //             course.getTitle(),
            //             course.getDescription(),
            //             course.getCategory()
            //         );
            //         course.setTitle(optimizedInfo.getOrDefault("optimized_title", course.getTitle()));
            //         course.setDescription(optimizedInfo.getOrDefault("optimized_description", course.getDescription()));
            //     } catch (Exception aiEx) {
            //         System.err.println("AI优化失败: " + aiEx.getMessage());
            //     }
            // }
            Course createdCourse = courseManagerService.createCourse(course);
            Map<String, Object> result = new HashMap<>();
            result.put("course", createdCourse);
            result.put("videoUrl", createdCourse.getVideoUrl());
            result.put("imageUrl", createdCourse.getImageUrl());
            return ResponseEntity.ok(Result.success("课程创建成功", result));
        } catch (Exception e) {
            e.printStackTrace(); // 强制输出异常到控制台
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("课程创建失败: " + e.getMessage()));
        }
    }

    /**
     * AI优化课程信息（预览）
     */
    @PostMapping("/optimize-preview")
    public ResponseEntity<Result> optimizeCoursePreview(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String category) {
        try {
            Map<String, String> optimizedInfo = courseOptimizationService.optimizeCourseInfo(title, description, category);
            return ResponseEntity.ok(Result.success("AI优化预览", optimizedInfo));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail("AI优化失败: " + e.getMessage()));
        }
    }

    /**
     * 获取课程详情（已发布所有人可查，驳回仅创建者可查）
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result> getCourseDetail(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        try {
            Course course = courseService.getCourseDetail(id);
            if ("PUBLISHED".equals(course.getStatus())) {
                return ResponseEntity.ok(Result.success("操作成功", course));
            } else if ("REJECTED".equals(course.getStatus()) && userId != null && userId.equals(course.getInstructorId())) {
                return ResponseEntity.ok(Result.success("操作成功", course));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Result.fail("无权限访问该课程"));
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.success("课程不存在", null));
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
     * 获取课程列表（只返回已发布课程）
     */
    @GetMapping("/list")
    public ResponseEntity<Result> getCourseList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String sortBy) {
        try {
            List<Course> allCourses = courseService.listPublishedCourses();
            
            // 过滤课程
            List<Course> filteredCourses = allCourses.stream()
                .filter(course -> {
                    // 关键词搜索
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String kw = keyword.toLowerCase();
                        return course.getTitle().toLowerCase().contains(kw) ||
                               course.getDescription().toLowerCase().contains(kw) ||
                               (course.getCategory() != null && course.getCategory().toLowerCase().contains(kw));
                    }
                    return true;
                })
                .filter(course -> {
                    // 分类过滤
                    if (category != null && !category.trim().isEmpty()) {
                        return category.equals(course.getCategory());
                    }
                    return true;
                })
                .filter(course -> {
                    // 难度过滤
                    if (level != null && !level.trim().isEmpty()) {
                        // 这里可以根据实际需求调整难度判断逻辑
                        return level.equals(course.getLevel());
                    }
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
            
            // 排序
            if (sortBy != null) {
                switch (sortBy) {
                    case "latest":
                        filteredCourses.sort((a, b) -> b.getId().compareTo(a.getId()));
                        break;
                    case "popular":
                        filteredCourses.sort((a, b) -> Integer.compare(b.getViewCount() != null ? b.getViewCount() : 0, 
                                                                      a.getViewCount() != null ? a.getViewCount() : 0));
                        break;
                    case "rating":
                        filteredCourses.sort((a, b) -> {
                            BigDecimal ratingA = a.getRating() != null ? a.getRating() : BigDecimal.ZERO;
                            BigDecimal ratingB = b.getRating() != null ? b.getRating() : BigDecimal.ZERO;
                            return ratingB.compareTo(ratingA);
                        });
                        break;
                    case "price-asc":
                        filteredCourses.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
                        break;
                    case "price-desc":
                        filteredCourses.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
                        break;
                }
            }
            
            // 分页
            int total = filteredCourses.size();
            int start = (page - 1) * size;
            int end = Math.min(start + size, total);
            
            List<Course> pageCourses = start < total ? filteredCourses.subList(start, end) : new ArrayList<>();
            
            // 构建分页响应
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("content", pageCourses);
            pageInfo.put("totalElements", total);
            pageInfo.put("totalPages", (int) Math.ceil((double) total / size));
            pageInfo.put("currentPage", page);
            pageInfo.put("pageSize", size);
            pageInfo.put("hasNext", end < total);
            pageInfo.put("hasPrevious", page > 1);
            
            return ResponseEntity.ok(Result.success("获取成功", pageInfo));
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
        course1.setImageUrl("https://via.placeholder.com/300x200");
        course1.setVideoUrl("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4");
        course1.setDuration(120);
        course1.setPrice(new BigDecimal("0.0"));
        course1.setInstructorId(1L);
        course1.setCategory("编程开发");
        course1.setStatus("PUBLISHED"); // 1表示已发布
        courses.add(course1);

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Spring Boot实战");
        course2.setDescription("Spring Boot框架开发实战课程");
        course2.setImageUrl("https://via.placeholder.com/300x200");
        course2.setVideoUrl("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4");
        course2.setDuration(180);
        course2.setPrice(new BigDecimal("99.0"));
        course2.setInstructorId(2L);
        course2.setCategory("框架开发");
        course2.setStatus("PUBLISHED"); // 1表示已发布
        courses.add(course2);

        Course course3 = new Course();
        course3.setId(3L);
        course3.setTitle("Vue.js前端开发");
        course3.setDescription("Vue.js前端框架开发教程");
        course3.setImageUrl("https://via.placeholder.com/300x200");
        course3.setVideoUrl("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4");
        course3.setDuration(150);
        course3.setPrice(new BigDecimal("79.0"));
        course3.setInstructorId(3L);
        course3.setCategory("前端开发");
        course3.setStatus("PUBLISHED"); // 1表示已发布
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
    @PostMapping("/{id}/submit-review")
    public ResponseEntity<Result> submitCourseForReview(@PathVariable Long id) {
        boolean success = courseService.submitCourseForReview(id);
        if (success) {
            return ResponseEntity.ok(Result.success("课程已提交审核", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail("课程提交审核失败"));
        }
    }

    /**
     * 管理员审核通过课程
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Result> approveCourse(@PathVariable Long id, @RequestParam Long adminId) {
        boolean success = courseService.approveCourse(id, adminId);
        if (success) {
            return ResponseEntity.ok(Result.success("课程审核通过", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail("课程审核通过失败"));
        }
    }

    /**
     * 管理员审核拒绝课程
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Result> rejectCourse(@PathVariable Long id, @RequestParam Long adminId, @RequestParam String reason) {
        boolean success = courseService.rejectCourse(id, adminId, reason);
        if (success) {
            return ResponseEntity.ok(Result.success("课程审核拒绝", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail("课程审核拒绝失败"));
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
     * 企业用户获取自己所有课程（草稿/待审核/已发布/驳回）
     */
    @GetMapping("/my")
    public ResponseEntity<Result> getMyCourses(@RequestParam Long userId) {
        try {
            List<Course> myCourses = courseService.listCoursesByUser(userId);
            return ResponseEntity.ok(Result.success("获取成功", myCourses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("获取我的课程失败: " + e.getMessage()));
        }
    }

    /**
     * 获取我的课程统计
     */
    @GetMapping("/my/stats")
    public Result getMyCourseStats(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = null;
        try {
            userId = jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return Result.fail("无法解析用户身份，请重新登录");
        }
        // 统计我的课程数
        int totalCourses = 0;
        try {
            totalCourses = courseHistoryService.getUserHistory(userId).size();
        } catch (Exception e) {}
        // 证书数（如有证书表可查，否则先 mock）
        int certificates = 0; // TODO: 查询证书表
        // 学习时长、平均进度（如有学习记录表可查，否则先 mock）
        int totalTime = 0; // TODO: 查询学习时长
        int avgProgress = 0; // TODO: 查询平均进度
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCourses", totalCourses);
        stats.put("certificates", certificates);
        stats.put("totalTime", totalTime);
        stats.put("avgProgress", avgProgress);
        return Result.success("操作成功", stats);
    }

    /**
     * 获取课程章节
     */
    @GetMapping("/{id}/chapters")
    public ResponseEntity<Result> getCourseChapters(@PathVariable Long id) {
        return ResponseEntity.ok(Result.success(chapterService.getChaptersByCourseId(id)));
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

    /**
     * 查询课程审核历史
     */
    @GetMapping("/{id}/review-history")
    public ResponseEntity<Result> getReviewHistory(@PathVariable Long id) {
        List<CourseReviewHistory> historyList = reviewHistoryDao.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CourseReviewHistory>()
                .eq("course_id", id)
                .orderByDesc("review_time")
        );
        return ResponseEntity.ok(Result.success(historyList));
    }

    /**
     * 视频流接口，支持断点续传和权限校验
     */
    @GetMapping("/{id}/video")
    public void streamVideo(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Course course;
        try {
            course = courseService.getCourseDetail(id);
        } catch (com.cemenghui.course.service.NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Course not found");
            return;
        }
        if (course == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Course not found");
            return;
        }
        String videoUrl = course.getVideoUrl();
        if (videoUrl == null || videoUrl.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // 从MinIO获取视频文件
        String objectName;
        if (videoUrl.contains("/course-files/")) {
            // 如果是完整的MinIO URL，提取对象名
            objectName = videoUrl.replaceFirst("^.*/course-files/", "");
        } else {
            // 如果只是对象名，直接使用
            objectName = videoUrl;
        }
        
        try {
            // 获取文件大小
            long fileSize = minioServiceImpl.getFileSize(objectName);
            if (fileSize == 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
            
            // 支持 HTTP Range
        String range = request.getHeader("Range");
            long start = 0, end = fileSize - 1;
        if (range != null && range.startsWith("bytes=")) {
            String[] parts = range.replace("bytes=", "").split("-");
            try {
                start = Long.parseLong(parts[0]);
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    end = Long.parseLong(parts[1]);
                }
            } catch (NumberFormatException ignore) {}
        }
            if (end >= fileSize) end = fileSize - 1;
            if (start > end) start = 0;
            
        long contentLength = end - start + 1;
        response.setStatus(range == null ? 200 : 206);
        response.setContentType("video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(contentLength));
            response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));
            
            // 从MinIO读取并输出视频流
            try (InputStream is = minioServiceImpl.downloadFile(objectName, start, end); 
                 OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            long toRead = contentLength;
            int len;
            while (toRead > 0 && (len = is.read(buffer, 0, (int)Math.min(buffer.length, toRead))) != -1) {
                os.write(buffer, 0, len);
                toRead -= len;
            }
            os.flush();
        }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Video streaming error: " + e.getMessage());
        }
    }

    /**
     * 获取热门搜索关键词
     */
    @GetMapping("/search/hot-keywords")
    public ResponseEntity<Result> getHotKeywords() {
        try {
            // 临时返回默认数据，避免 500 错误
            List<String> keywords = java.util.Arrays.asList("Java", "前端", "AI", "大数据", "Python");
            return ResponseEntity.ok(Result.success(keywords));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("获取热搜关键词失败: " + e.getMessage()));
        }
    }

    /**
     * 课程AI问答接口
     */
    @PostMapping("/{id}/ai-qna")
    public ResponseEntity<Result> askCourseQuestion(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String questionContent = (String) request.get("question");
            Long userId = Long.valueOf(request.get("userId").toString());
            
            if (questionContent == null || questionContent.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Result.fail("问题内容不能为空"));
            }
            
            // 创建问题对象
            Question question = new Question();
            question.setCourseId(id);
            question.setUserId(userId);
            question.setQuestion(questionContent);
            question.setCreatedAt(java.time.LocalDateTime.now());
            
            // 保存问题并生成AI回复
            Question savedQuestion = qnaService.askQuestion(question);
            String aiReply = qnaService.autoReply(savedQuestion);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("question", savedQuestion);
            response.put("aiReply", aiReply);
            
            return ResponseEntity.ok(Result.success("AI问答成功", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("AI问答失败: " + e.getMessage()));
        }
    }

    /**
     * 获取课程AI问答历史
     */
    @GetMapping("/{id}/ai-qna")
    public ResponseEntity<Result> getCourseAIQna(@PathVariable Long id,
                                                @RequestParam(defaultValue = "1") long current,
                                                @RequestParam(defaultValue = "10") long size) {
        try {
            Page<Question> page = new Page<>(current, size);
            IPage<Question> questions = qnaService.getQuestionsByCoursePaged(id, page);
            return ResponseEntity.ok(Result.success("获取AI问答历史成功", questions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("获取AI问答历史失败: " + e.getMessage()));
        }
    }

    /**
     * 重新生成AI回复
     */
    @PostMapping("/{courseId}/ai-qna/{questionId}/regenerate")
    public ResponseEntity<Result> regenerateAIReply(@PathVariable Long courseId, @PathVariable Long questionId) {
        try {
            Question question = qnaService.getQuestionById(questionId);
            if (question == null) {
                return ResponseEntity.notFound().build();
            }
            
            String newReply = qnaService.autoReply(question);
            return ResponseEntity.ok(Result.success("重新生成AI回复成功", newReply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("重新生成AI回复失败: " + e.getMessage()));
        }
    }
} 