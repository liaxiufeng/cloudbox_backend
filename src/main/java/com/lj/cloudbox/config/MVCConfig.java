package com.lj.cloudbox.config;

import com.lj.cloudbox.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {
    /**
     * 拦截器
     * 1.编写一个拦截器实现HandlerInterceptor接口
     * 2.将拦截器注册到容器中WebMvcConfigurer的addInterceptors方法
     * 3.指定拦截器规则（/**会拦截静态资源）
     *
     */
    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login","/register","/public/**","/test/**");
    }

}
