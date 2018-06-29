package com.gimplatform.core.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.UserInfo;

/**
 * 日志服务类
 * @author zzd
 */
public interface LogInfoService {

    /**
     * 获取返回分页数据的日志列表
     * @param page
     * @param userInfo
     * @param params
     * @return
     */
    public JSONObject getLogList(Pageable page, UserInfo userInfo, Map<String, Object> params) ;

    /**
     * 保存日志
     * @param request
     * @param title
     */
    public void saveLog(HttpServletRequest request, String operateType, String logDesc) throws IOException ;

    /**
     * 保存日志
     * @param request
     * @param handler
     * @param ex
     * @param operateType
     * @param logDesc
     */
    public void saveLog(HttpServletRequest request, Object handler, Exception ex, String operateType, String logDesc) throws IOException;

}
