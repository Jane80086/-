# 跨域配置说明文档

## 🚀 概述

本系统已配置全局跨域处理，允许前端应用从不同域名访问API接口。

## 📋 配置内容

### 1. 跨域配置类 (`CorsConfig.java`)

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")        // 允许所有域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")               // 允许所有请求头
                .allowCredentials(true)            // 允许携带cookie
                .maxAge(3600);                     // 预检请求缓存时间
    }
}
```

### 2. Spring Security配置 (`SecurityConfig.java`)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()                      // 禁用CSRF
            .cors().configurationSource(corsConfigurationSource)  // 启用跨域
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/api/**").permitAll()  // 允许所有API访问
                .anyRequest().authenticated();
        
        return http.build();
    }
}
```

## 🔧 配置详情

### 允许的域名
- **配置**: `allowedOriginPatterns("*")`
- **说明**: 允许所有域名访问，包括本地开发环境

### 允许的HTTP方法
- **GET**: 获取数据
- **POST**: 创建数据
- **PUT**: 更新数据
- **DELETE**: 删除数据
- **OPTIONS**: 预检请求

### 允许的请求头
- **配置**: `allowedHeaders("*")`
- **说明**: 允许所有请求头，包括自定义头部

### Cookie支持
- **配置**: `allowCredentials(true)`
- **说明**: 允许携带认证信息（cookie、Authorization等）

### 预检请求缓存
- **配置**: `maxAge(3600)`
- **说明**: 预检请求结果缓存1小时，减少重复请求

## 🧪 测试方法

### 1. 使用测试页面
访问: `http://localhost:8080/cors-test.html`

这个页面提供了完整的跨域测试功能，包括：
- 系统健康检查
- MCP服务器状态
- 课程列表获取
- 课程创建
- 课程搜索
- MCP内容优化
- 推荐课程获取

### 2. 使用curl命令
```bash
# 测试GET请求
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: X-Requested-With" \
     -X OPTIONS \
     http://localhost:8080/api/stats/health

# 测试POST请求
curl -X POST \
     -H "Origin: http://localhost:3000" \
     -H "Content-Type: application/json" \
     -d '{"title":"测试课程"}' \
     http://localhost:8080/api/course/create
```

### 3. 使用JavaScript
```javascript
// 测试跨域请求
fetch('http://localhost:8080/api/stats/health', {
    method: 'GET',
    headers: {
        'Content-Type': 'application/json'
    }
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

## 🌐 支持的场景

### 1. 前端开发环境
- **React**: `http://localhost:3000`
- **Vue**: `http://localhost:8080`
- **Angular**: `http://localhost:4200`

### 2. 移动端应用
- **iOS App**: 通过HTTP请求访问
- **Android App**: 通过HTTP请求访问

### 3. 第三方集成
- **Postman**: API测试工具
- **其他服务**: 微服务间调用

## 🚨 安全注意事项

### 1. 生产环境配置
在生产环境中，建议将 `allowedOriginPatterns("*")` 改为具体的域名：

```java
.allowedOriginPatterns(
    "https://yourdomain.com",
    "https://www.yourdomain.com",
    "https://api.yourdomain.com"
)
```

### 2. 请求头限制
如果需要限制特定的请求头，可以修改配置：

```java
.allowedHeaders(
    "Content-Type",
    "Authorization",
    "X-Requested-With"
)
```

### 3. 方法限制
如果只需要特定的HTTP方法，可以限制：

```java
.allowedMethods("GET", "POST")  // 只允许GET和POST
```

## 🔄 常见问题解决

### 1. 跨域请求被阻止
**问题**: 浏览器显示CORS错误
**解决**: 检查CorsConfig是否正确配置并生效

### 2. 预检请求失败
**问题**: OPTIONS请求返回错误
**解决**: 确保Spring Security配置中启用了cors

### 3. 认证信息丢失
**问题**: Cookie或Authorization头被忽略
**解决**: 确保 `allowCredentials(true)` 已配置

### 4. 特定域名访问失败
**问题**: 某些域名无法访问
**解决**: 检查 `allowedOriginPatterns` 配置

## 📝 配置验证

### 1. 检查响应头
成功的跨域请求应该包含以下响应头：
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

### 2. 浏览器开发者工具
在浏览器开发者工具的Network标签中查看：
- 请求是否包含正确的Origin头
- 响应是否包含正确的CORS头
- 是否有预检请求（OPTIONS）

## 🎯 最佳实践

1. **开发环境**: 使用 `*` 允许所有域名
2. **生产环境**: 明确指定允许的域名
3. **安全考虑**: 只允许必要的HTTP方法和请求头
4. **性能优化**: 合理设置预检请求缓存时间
5. **监控**: 定期检查跨域请求的日志

## 📚 相关文档

- [Spring Boot CORS配置](https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html)
- [MDN CORS文档](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Spring Security CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html) 