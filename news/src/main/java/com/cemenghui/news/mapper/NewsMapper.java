package com.cemenghui.news.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.news.entity.News;
import com.cemenghui.news.dto.SearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface NewsMapper extends BaseMapper<News> {

    @Select("SELECT * FROM news WHERE id = #{id} AND is_deleted = 0")
    News selectById(@Param("id") Long id);

    Page<News> findByCondition(Page<News> page, @Param("request") SearchRequest request, @Param("status") Integer status, @Param("currentUserId") Long currentUserId);
Long countByCondition(@Param("request") SearchRequest request, @Param("status") Integer status);
    Page<News> findByUserId(Page<News> page, @Param("userId") Long userId);

    @Update("UPDATE news SET view_count = view_count + 1 WHERE id = #{newsId}")
    void updateViewCount(@Param("newsId") Long newsId);

    @Update("UPDATE news SET is_deleted = 1 WHERE id = #{newsId} AND user_id = #{userId}")
    int softDelete(@Param("newsId") Long newsId, @Param("userId") Long userId);

    Page<News> findPendingNews(Page<News> page);

    Long countByStatus(@Param("status") Integer status);

    List<News> findPopularNews(@Param("limit") Integer limit, @Param("status") Integer status);

    @Select("SELECT COUNT(*) > 0 FROM news WHERE id = #{newsId} AND user_id = #{userId} AND is_deleted = 0")
    boolean existsByIdAndUserId(@Param("newsId") Long newsId, @Param("userId") Long userId);

    Map<String, Object> getAdminBasicStatistics(@Param("pendingStatus") Integer pendingStatus, @Param("publishedStatus") Integer publishedStatus, @Param("rejectedStatus") Integer rejectedStatus);

    List<Map<String, Object>> getViewTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<News> getHotNews(@Param("limit") Integer limit, @Param("status") Integer status);
}