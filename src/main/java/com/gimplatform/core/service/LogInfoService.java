package com.gimplatform.core.service;

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
     * 保存日志
     * @param request
     * @param title
     */
    public void saveLog(HttpServletRequest request, String title);

    /**
     * 保存日志
     * @param request
     * @param handler
     * @param ex
     * @param title
     */
    public void saveLog(HttpServletRequest request, Object handler, Exception ex, String title);

    /**
     * 获取返回分页数据的日志列表
     * @param page
     * @param userInfo
     * @param tenantsId
     * @param organizerId
     * @param title
     * @param beginTime
     * @param endTime
     * @return
     */
    public JSONObject getLogList(Pageable page, UserInfo userInfo, Long tenantsId, Long organizerId, String title, String beginTime, String endTime);

}
