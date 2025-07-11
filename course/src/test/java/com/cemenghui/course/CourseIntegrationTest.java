package com.cemenghui.course;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CourseIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCourseList() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/course/list", String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testCourseFullFlowWithDetailLog() {
        try {
            // 1. 创建课程
            Map<String, Object> course = new HashMap<>();
            course.put("title", "自动化测试课程");
            course.put("description", "自动化测试描述");
            course.put("instructorId", 1);
            course.put("price", 0);
            course.put("duration", 60);
            course.put("category", "测试分类");
            course.put("status", "DRAFT");
            ResponseEntity<Map> createResp = restTemplate.postForEntity("/api/course/create", course, Map.class);
            System.out.println("创建课程返回: " + createResp);
            assertThat(createResp.getStatusCodeValue()).isEqualTo(200);
            Map<String, Object> createBody = (Map<String, Object>) createResp.getBody().get("data");
            Integer courseId = (Integer) createBody.get("id");
            assertThat(courseId).isNotNull();

            // 2. 查询详情
            ResponseEntity<Map> detailResp = restTemplate.getForEntity("/api/course/" + courseId, Map.class);
            System.out.println("课程详情返回: " + detailResp);
            assertThat(detailResp.getStatusCodeValue()).isEqualTo(200);
            assertThat(detailResp.getBody().get("msg")).as("课程详情msg").isNotNull();

            // 3. 更新课程
            course.put("title", "更新后的课程");
            HttpEntity<Map<String, Object>> updateReq = new HttpEntity<>(course);
            ResponseEntity<Map> updateResp = restTemplate.exchange("/api/course/" + courseId, HttpMethod.PUT, updateReq, Map.class);
            System.out.println("更新课程返回: " + updateResp);
            assertThat(updateResp.getStatusCodeValue()).isEqualTo(200);
            assertThat(updateResp.getBody().get("msg")).as("更新课程msg").isNotNull();

            // 4. 提交审核
            ResponseEntity<Map> submitResp = restTemplate.postForEntity("/api/course/" + courseId + "/submit-review", null, Map.class);
            System.out.println("提交审核返回: " + submitResp);
            assertThat(submitResp.getStatusCodeValue()).isEqualTo(200);
            assertThat(submitResp.getBody().get("msg")).as("提交审核msg").isNotNull();

            // 5. 审核通过
            ResponseEntity<Map> approveResp = restTemplate.postForEntity("/api/course/" + courseId + "/approve?adminId=1", null, Map.class);
            System.out.println("审核通过返回: " + approveResp);
            assertThat(approveResp.getStatusCodeValue()).isEqualTo(200);
            assertThat(approveResp.getBody().get("msg")).as("审核通过msg").isNotNull();

            // 6. 删除课程
            ResponseEntity<Map> deleteResp = restTemplate.exchange("/api/course/" + courseId, HttpMethod.DELETE, null, Map.class);
            System.out.println("删除课程返回: " + deleteResp);
            assertThat(deleteResp.getStatusCodeValue()).isEqualTo(200);
            assertThat(deleteResp.getBody().get("msg")).as("删除课程msg").isNotNull();
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(false).as("出现异常: " + e.getMessage()).isTrue();
        }
    }

    @Test
    void testCourseExtraInterfaces() {
        // 1. 课程列表
        ResponseEntity<Map> listResp = restTemplate.getForEntity("/api/course/list", Map.class);
        System.out.println("课程列表: " + listResp);
        assertThat(listResp.getStatusCodeValue()).isEqualTo(200);

        // 2. 搜索课程
        ResponseEntity<Map> searchResp = restTemplate.getForEntity("/api/course/search?keyword=Java", Map.class);
        System.out.println("搜索课程: " + searchResp);
        assertThat(searchResp.getStatusCodeValue()).isEqualTo(200);

        // 3. 热门搜索趋势
        ResponseEntity<Map> trendsResp = restTemplate.getForEntity("/api/course/trends", Map.class);
        System.out.println("热门搜索: " + trendsResp);
        assertThat(trendsResp.getStatusCodeValue()).isEqualTo(200);

        // 4. 获取我的课程
        ResponseEntity<Map> myResp = restTemplate.getForEntity("/api/course/my", Map.class);
        System.out.println("我的课程: " + myResp);
        assertThat(myResp.getStatusCodeValue()).isEqualTo(200);

        // 5. 课程进度相关
        ResponseEntity<Map> progressResp = restTemplate.getForEntity("/api/course/1/progress", Map.class);
        System.out.println("课程进度: " + progressResp);
        assertThat(progressResp.getStatusCodeValue()).isEqualTo(200);

        Map<String, Object> progressData = new HashMap<>();
        progressData.put("progress", 0.5);
        ResponseEntity<Map> saveProgressResp = restTemplate.postForEntity("/api/course/1/progress", progressData, Map.class);
        System.out.println("保存进度: " + saveProgressResp);
        assertThat(saveProgressResp.getStatusCodeValue()).isEqualTo(200);

        // 6. 记录播放行为
        Map<String, Object> playAction = new HashMap<>();
        playAction.put("action", "play");
        playAction.put("currentTime", 10.5);
        playAction.put("timestamp", System.currentTimeMillis());
        ResponseEntity<Map> playResp = restTemplate.postForEntity("/api/course/1/record", playAction, Map.class);
        System.out.println("记录播放行为: " + playResp);
        assertThat(playResp.getStatusCodeValue()).isEqualTo(200);

        // 7. 课程笔记相关
        ResponseEntity<Map> notesResp = restTemplate.getForEntity("/api/course/1/notes", Map.class);
        System.out.println("课程笔记: " + notesResp);
        assertThat(notesResp.getStatusCodeValue()).isEqualTo(200);

        Map<String, Object> noteData = new HashMap<>();
        noteData.put("timestamp", 100);
        noteData.put("content", "自动化测试笔记");
        ResponseEntity<Map> addNoteResp = restTemplate.postForEntity("/api/course/1/notes", noteData, Map.class);
        System.out.println("添加笔记: " + addNoteResp);
        assertThat(addNoteResp.getStatusCodeValue()).isEqualTo(200);

        // 8. 视频流接口（仅测试接口可访问，实际内容需本地有视频文件）
        ResponseEntity<byte[]> videoResp = restTemplate.getForEntity("/api/course/1/video", byte[].class);
        System.out.println("视频流接口状态: " + videoResp.getStatusCodeValue());
        assertThat(videoResp.getStatusCodeValue()).isIn(200, 206, 404); // 200/206为正常，404为无视频
    }
} 