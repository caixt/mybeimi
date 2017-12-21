package com.github.cxt.mybeimi.web.handler.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CrossInterceptorHandler extends HandlerInterceptorAdapter {
	
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    	response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, accept, authorization, content-type");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return true ; 
    }

    public void postHandle(HttpServletRequest arg0, HttpServletResponse response, Object arg2,
            ModelAndView view) throws Exception {
    	
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

}