package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.impl.CourseManagerServiceImpl;
import com.cemenghui.course.service.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseManagerServiceImpl courseManagerService;

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
            List<Course> courses = courseService.listCourses();
            return ResponseEntity.ok(Result.success(courses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("获取课程列表失败: " + e.getMessage()));
        }
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
} 