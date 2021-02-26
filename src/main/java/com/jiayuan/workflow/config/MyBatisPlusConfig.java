package com.jiayuan.workflow.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luoyj
 * @date 2019/11/26.
 * 此出 添加了 @MapperScan("com.jiayuan.workflow.dao") 启动类上则不需要再次添加
 */
@Configuration
//@MapperScan("com.jiayuan.workflow.dao")
public class MyBatisPlusConfig {
	
    /**
     * mybatis-plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setDbType(DbType.MYSQL);
        paginationInterceptor.setDialect(new MySqlDialect());
        return paginationInterceptor;
    }

}