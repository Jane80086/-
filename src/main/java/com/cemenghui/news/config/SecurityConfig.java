package com.cemenghui.news.config;

import com.cemenghui.news.security.AuthEntryPointJwt;
import com.cemenghui.news.security.JwtAuthenticationFilter;
import com.cemenghui.news.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder; // 导入此包
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity; // 修改为EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter; // 导入此包
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类，定义了认证方式、授权规则和过滤器链。
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用@PreAuthorize注解，2.x版本使用此注解
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter { // 2.x版本需要继承WebSecurityConfigurerAdapter

    private final CustomUserDetailsService userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置密码编码器。
     * 使用BCryptPasswordEncoder进行密码加密。
     * @return PasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置DaoAuthenticationProvider，用于从UserDetailsService加载用户详情并验证密码。
     * @return DaoAuthenticationProvider实例
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // 设置自定义的用户详情服务
        authProvider.setPasswordEncoder(passwordEncoder()); // 设置密码编码器
        return authProvider;
    }

    /**
     * 配置AuthenticationManager。
     * 在2.x版本中，通常通过重写configure(AuthenticationManagerBuilder)来获取。
     * @return AuthenticationManager实例
     * @throws Exception 如果获取失败
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置全局的AuthenticationManagerBuilder。
     * @param auth AuthenticationManagerBuilder对象
     * @throws Exception 如果配置失败
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     * 配置HTTP安全。
     * 定义了HTTP请求的授权规则、会话管理策略和自定义过滤器。
     * @param http HttpSecurity对象
     * @throws Exception 如果配置失败
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF，因为JWT是无状态的，不需要依赖session来防范CSRF攻击
                .csrf().disable()
                // 配置异常处理，特别是未认证的入口点
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                // 配置会话管理策略为无状态，Spring Security将不会创建或使用HTTP会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 配置请求授权规则
                .authorizeRequests() // 2.x版本使用authorizeRequests()
                // 允许所有用户访问认证相关的接口
                .antMatchers("/api/auth/**").permitAll() // 2.x版本使用antMatchers()
                // 允许公共访问的新闻搜索接口
                .antMatchers("/api/news/search").permitAll()
                // 允许公共访问的新闻详情接口
                .antMatchers("/api/news/{newsId}").permitAll()
                // 允许公共访问的热门新闻接口
                .antMatchers("/api/news/popular").permitAll()
                // 要求拥有"ADMIN"角色才能访问/api/admin/**路径下的接口
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                // 要求拥有"ENTERPRISE"或"ADMIN"角色才能访问/api/enterprise/**路径下的接口
                .antMatchers("/api/enterprise/**").hasAnyRole("ENTERPRISE", "ADMIN")
                // 其他所有请求都需要认证
                .anyRequest().authenticated().and();

        // 在Spring Security的UsernamePasswordAuthenticationFilter之前添加JWT认证过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}