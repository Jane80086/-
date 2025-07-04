package com.cemenghui.news.mapper;

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

    /**
     * 根据条件搜索新闻，增加状态过滤和当前用户ID过滤（用于企业用户查看所有状态的自己新闻）
     *
     * @param page 分页对象
     * @param request 搜索请求DTO
     * @param status 可选的新闻状态 (null表示不限制状态)
     * @param currentUserId 可选的当前用户ID (用于联合查询自己发布的所有状态新闻)
     */
    Page<News> findByCondition(Page<News> page,
                               @Param("request") SearchRequest request,
                               @Param("status") Integer status,
                               @Param("currentUserId") Long currentUserId);

    /**
     * 根据用户ID查询新闻列表 (企业用户查看自己发布的所有动态)
     */
    Page<News> findByUserId(Page<News> page, @Param("userId") Long userId);

    /**
     * 更新浏览次数
     */
    @Update("UPDATE news SET view_count = view_count + 1 WHERE id = #{newsId}")
    void updateViewCount(@Param("newsId") Long newsId);

    /**
     * 软删除新闻 (仅限新闻所有者，管理员删除请直接更新is_deleted字段)
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
     * 根据状态统计新闻数量 (可以统计所有状态，传入null)
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * 查询热门新闻，增加状态过滤 (此方法将仅根据view_count排序，且只返回News核心字段)
     */
    List<News> findPopularNews(@Param("limit") Integer limit, @Param("status") Integer status);

    /**
     * 检查新闻是否属于指定用户
     */
    @Select("SELECT COUNT(*) > 0 FROM news WHERE id = #{newsId} AND user_id = #{userId} AND is_deleted = 0")
    boolean existsByIdAndUserId(@Param("newsId") Long newsId, @Param("userId") Long userId);

    /**
     * 获取管理员仪表盘基础统计数据
     * 返回一个包含所有所需统计项的Map
     */
    Map<String, Object> getAdminBasicStatistics(@Param("pendingStatus") Integer pendingStatus,
                                                @Param("approvedStatus") Integer approvedStatus,
                                                @Param("rejectedStatus") Integer rejectedStatus,
                                                @Param("draftStatus") Integer draftStatus);

    /**
     * 获取指定日期范围内的浏览量趋势数据
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate 结束日期 (YYYY-MM-DD)
     * @return 每日浏览量列表，例如 [{date: "2023-01-01", views: 100}, ...]
     */
    List<Map<String, Object>> getViewTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取热门动态列表（管理员视角，仅根据浏览量排序）
     * @param limit 返回数量限制
     * @param status 动态状态，null表示所有状态，否则根据状态码过滤
     * @return 热门动态列表 (包含 id, title, view_count)
     */
    List<News> getHotNews(@Param("limit") Integer limit, @Param("status") Integer status);
}