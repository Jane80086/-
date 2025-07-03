package com.cemenghui.meeting.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus配置类
 * 配置分页插件、乐观锁插件、防止全表更新与删除插件等
 */
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    /**
     * 配置MyBatis-Plus拦截器
     * 包含分页插件、乐观锁插件、防止全表更新与删除插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件 - 配置数据库类型为KingBase
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(1000L);
        // 设置请求的页面大于最大页后操作，true调回到首页，false继续请求
        paginationInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        // 乐观锁插件
        OptimisticLockerInnerInterceptor optimisticLockerInterceptor = new OptimisticLockerInnerInterceptor();
        interceptor.addInnerInterceptor(optimisticLockerInterceptor);
        
        // 防止全表更新与删除插件
        BlockAttackInnerInterceptor blockAttackInterceptor = new BlockAttackInnerInterceptor();
        interceptor.addInnerInterceptor(blockAttackInterceptor);
        
        return interceptor;
    }

    /**
     * 配置全局配置
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        
        // 设置数据库相关配置
        globalConfig.setDbConfig(new com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig()
                // 设置逻辑删除字段
                .setLogicDeleteField("deleted")
                // 设置逻辑删除值
                .setLogicDeleteValue("1")
                // 设置逻辑未删除值
                .setLogicNotDeleteValue("0")
                // 设置主键类型
                .setIdType(com.baomidou.mybatisplus.annotation.IdType.AUTO)
        );
        
        return globalConfig;
    }
} 