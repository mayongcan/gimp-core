package com.gimplatform.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 用于拦截http post的参数，获取后保存进缓存，使用后返回给spring mvc
 * @author zzd
 *
 */
public class PostParamsHandleFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String reqMethod = httpServletRequest.getMethod();
        if("POST".equals(reqMethod)){
            ServletRequest requestWrapper = null;
            if(servletRequest instanceof HttpServletRequest) {
                requestWrapper = new RequestBodyHttpServletRequestWrapper((HttpServletRequest) servletRequest);
            }
            if(requestWrapper == null) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                filterChain.doFilter(requestWrapper, servletResponse);
            }   
        }else {
            //如果不是POST请求则放行
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
