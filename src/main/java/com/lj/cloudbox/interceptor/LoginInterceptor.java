package com.lj.cloudbox.interceptor;

import com.lj.cloudbox.pojo.User;
import com.lj.cloudbox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) return true;
        String token = request.getHeader("token");

        String requestURI = request.getRequestURI();
        System.out.println("requestURI = " + requestURI);

        User user = userService.getUser(token);
        if(user != null) user.setPassword("隐私字段！！！");
        request.setAttribute("user",user);
        return StringUtils.hasLength(token) && user != null;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

}
