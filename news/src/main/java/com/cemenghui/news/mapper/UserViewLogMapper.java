package com.cemenghui.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.news.entity.UserViewLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserViewLogMapper extends BaseMapper<UserViewLog> {
    
    /**
     * 根据新闻ID查找浏览记录
     */
    @Select("SELECT * FROM user_view_logs WHERE resource_type = 'NEWS' AND resource_id = #{newsId} ORDER BY view_time DESC")
    List<UserViewLog> findByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 根据用户ID查找浏览记录
     */
    @Select("SELECT * FROM user_view_logs WHERE resource_type = 'NEWS' AND user_id = #{userId} ORDER BY view_time DESC")
    List<UserViewLog> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查找新闻的浏览次数
     */
    @Select("SELECT COUNT(*) FROM user_view_logs WHERE resource_type = 'NEWS' AND resource_id = #{newsId}")
    Long countViewsByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 查找用户浏览新闻的次数
     */
    @Select("SELECT COUNT(*) FROM user_view_logs WHERE resource_type = 'NEWS' AND user_id = #{userId} AND resource_id = #{newsId}")
    Long countViewsByUserAndNews(@Param("userId") Long userId, @Param("newsId") Long newsId);
}