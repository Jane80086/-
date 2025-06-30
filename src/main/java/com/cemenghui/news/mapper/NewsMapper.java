package com.cemenghui.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.news.entity.News;
import com.cemenghui.news.dto.SearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface NewsMapper extends BaseMapper<News> {

    /**
     * 根据条件搜索新闻
     */
    Page<News> findByCondition(Page<News> page, @Param("request") SearchRequest request);

    /**
     * 根据用户ID查询新闻列表
     */
    Page<News> findByUserId(Page<News> page, @Param("userId") Long userId);

    /**
     * 更新浏览次数
     */
    @Update("UPDATE news SET view_count = view_count + 1 WHERE id = #{newsId}")
    void updateViewCount(@Param("newsId") Long newsId);

    /**
     * 软删除新闻
     */
    @Update("UPDATE news SET is_deleted = 1, deleted_time = NOW() WHERE id = #{newsId} AND user_id = #{userId}")
    int softDelete(@Param("newsId") Long newsId, @Param("userId") Long userId);

    /**
     * 查询待审核新闻
     */
    Page<News> findPendingNews(Page<News> page);

    /**
     * 更新审核状态
     */
    @Update("UPDATE news SET status = #{status}, audit_comment = #{comment}, audit_user_id = #{auditUserId}, audit_time = NOW() WHERE id = #{newsId}")
    int updateAuditStatus(@Param("newsId") Long newsId, @Param("status") Integer status,
                          @Param("comment") String comment, @Param("auditUserId") Long auditUserId);

    /**
     * 根据状态统计新闻数量
     */
    @Select("SELECT COUNT(*) FROM news WHERE status = #{status} AND is_deleted = 0")
    Long countByStatus(@Param("status") Integer status);

    /**
     * 查询热门新闻
     */
    @Select("SELECT * FROM news WHERE status = 1 AND is_deleted = 0 ORDER BY view_count DESC LIMIT #{limit}")
    List<News> findPopularNews(@Param("limit") Integer limit);

    /**
     * 检查新闻是否属于指定用户
     */
    @Select("SELECT COUNT(*) > 0 FROM news WHERE id = #{newsId} AND user_id = #{userId} AND is_deleted = 0")
    boolean existsByIdAndUserId(@Param("newsId") Long newsId, @Param("userId") Long userId);

    /**
     * 获取基础统计数据
     */
    List<Map<String, Object>> getBasicStatistics();
}