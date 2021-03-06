package com.gimplatform.core.interceptor;

import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.gimplatform.core.annotation.LogConf;
import com.gimplatform.core.service.LogInfoService;
import com.gimplatform.core.utils.DateUtils;
import com.gimplatform.core.utils.SpringContextHolder;

/**
 * 日志拦截器
 * @author zzd
 * @version 2017-04-03
 */
public class LogInfoInterceptor implements HandlerInterceptor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("ThreadLocal StartTime");

    // 由于自动注入@Autowired获取为null，因此使用spring上下文获取
    private LogInfoService logInfoService = null;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (logger.isDebugEnabled()) {
            long beginTime = System.currentTimeMillis();// 1、开始时间
            startTimeThreadLocal.set(beginTime); // 线程绑定变量（该数据只有当前请求的线程可见）
            logger.info("开始计时: {}  URI: {}", new SimpleDateFormat("HH:mm:ss.SSS").format(beginTime), request.getRequestURI());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            logger.info("ViewName: " + modelAndView.getViewName());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //获取日志服务
        if (logInfoService == null)
            logInfoService = SpringContextHolder.getBean(LogInfoService.class);
        String operateType = "", logDesc = "";
        if (handler instanceof HandlerMethod) {
            //获取注解对象
            LogConf logConf = ((HandlerMethod) handler).getMethodAnnotation(LogConf.class);
            if(logConf != null) {
                operateType = logConf.operateType().getValue();
                logDesc = logConf.logDesc();
            }
        }
        // 保存日志
        logInfoService.saveLog(request, handler, ex, operateType, logDesc);

        // 打印JVM信息。
        if (logger.isDebugEnabled()) {
            long beginTime = startTimeThreadLocal.get();// 得到线程绑定的局部变量（开始时间）
            long endTime = System.currentTimeMillis(); // 2、结束时间
            logger.info("计时结束：{}  耗时：{}  URI: {}  最大内存: {}m  已分配内存: {}m  已分配内存中的剩余空间: {}m  最大可用内存: {}m", new SimpleDateFormat("HH:mm:ss.SSS").format(endTime), DateUtils.formatDateTime(endTime - beginTime), request.getRequestURI(),
                    Runtime.getRuntime().maxMemory() / 1024 / 1024, Runtime.getRuntime().totalMemory() / 1024 / 1024, Runtime.getRuntime().freeMemory() / 1024 / 1024,
                    (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1024 / 1024);
            // 删除线程变量中的数据，防止内存泄漏
            startTimeThreadLocal.remove();
        }
    }
}