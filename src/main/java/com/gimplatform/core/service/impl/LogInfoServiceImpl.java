package com.gimplatform.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.GlobalVal;
import com.gimplatform.core.entity.FuncInfo;
import com.gimplatform.core.entity.LogInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.repository.LogInfoRepository;
import com.gimplatform.core.service.LogInfoService;
import com.gimplatform.core.utils.DateUtils;
import com.gimplatform.core.utils.Exceptions;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.StringUtils;
import com.gimplatform.core.utils.UserAgentUtils;

/**
 * 日志消息服务类
 * @author zzd
 */
@Service
public class LogInfoServiceImpl implements LogInfoService {

    protected static final Logger logger = LogManager.getLogger(LogInfoServiceImpl.class);

    @Autowired
    private LogInfoRepository logInfoRepository;

    @Override
    public void saveLog(HttpServletRequest request, String title) {
        saveLog(request, null, null, title);
    }

    @Override
    public void saveLog(HttpServletRequest request, Object handler, Exception ex, String title) {
        UserInfo userInfo = SessionUtils.getUserInfo();
        Long userId = -1L;
        if (userInfo != null && userInfo.getUserId() != null) {
            userId = userInfo.getUserId();
        }
        // 新建日志记录
        LogInfo logInfo = new LogInfo();
        logInfo.setLogTitle(title);
        logInfo.setCreateBy(userId);
        logInfo.setCreateDate(DateUtils.dateFormat(new Date()));
        logInfo.setLogType(ex == null ? LogInfo.TYPE_ACCESS : LogInfo.TYPE_EXCEPTION);
        logInfo.setRemoteAddr(UserAgentUtils.getIpAddress(request));
        logInfo.setUserAgent(request.getHeader("user-agent"));
        logInfo.setReqeustUri(request.getRequestURI());
        logInfo.setParams(StringUtils.mapToString(request.getParameterMap()));
        logInfo.setMethod(request.getMethod());
        // 异步保存日志
        new SaveLogInfoThread(logInfo, handler, ex).start();
    }

    /**
     * 获取菜单名称路径（如：系统设置-机构用户-用户管理-编辑）
     * @param requestUri
     * @return
     */
    private String getMenuPath(String requestUri) {
        if (GlobalVal.funcInfoList == null || GlobalVal.funcInfoList.size() == 0) {
            return "";
        }
        FuncInfo funcInfo = null;
        // 先判断当前连接是否存在于权限列表中
        for (FuncInfo func : GlobalVal.funcInfoList) {
            if (func.getFuncLink().equals(requestUri) || func.getFuncFlag().equals(requestUri)) {
                funcInfo = func;
                break;
            }
        }
        // 递归获取标题
        return getMenuPathParent(funcInfo);
    }

    /**
     * 递归获取权限路径
     * @param list
     * @param funcInfo
     * @return
     */
    private String getMenuPathParent(FuncInfo funcInfo) {
        if (funcInfo == null)
            return "";
        // 如果父权限为根目录权限，则不用继续递归
        if (funcInfo.getParentFuncId().equals(1L))
            return funcInfo.getFuncName();
        String retVal = "";
        // 获取父权限的名称
        for (FuncInfo func : GlobalVal.funcInfoList) {
            if (func.getFuncId().equals(funcInfo.getParentFuncId())) {
                String tmpVal = getMenuPathParent(func);
                if (!"".equals(tmpVal))
                    retVal = tmpVal + ">>" + funcInfo.getFuncName();
                else
                    retVal = funcInfo.getFuncName();
                break;
            }
        }
        return retVal;
    }

    /**
     * 保存日志线程
     */
    public class SaveLogInfoThread extends Thread {

        private LogInfo logInfo;
        private Exception ex;

        public SaveLogInfoThread(LogInfo logInfo, Object handler, Exception ex) {
            super(SaveLogInfoThread.class.getSimpleName());
            this.logInfo = logInfo;
            this.ex = ex;
        }

        @Override
        public void run() {
            // 获取日志标题
            if (StringUtils.isBlank(logInfo.getLogTitle())) {
                logInfo.setLogTitle(getMenuPath(logInfo.getReqeustUri()));
            }
            // 如果有异常，设置异常信息
            logInfo.setException(Exceptions.getStackTraceAsString(ex));
            // 如果无标题并无异常日志，则不保存信息
            if (StringUtils.isBlank(logInfo.getLogTitle()) && StringUtils.isBlank(logInfo.getException())) {
                logger.debug("日志标题为空，不保存操作日志:" + logInfo.getReqeustUri());
                return;
            }
            // 保存日志信息
            logInfoRepository.save(logInfo);
        }
    }

    @Override
    public JSONObject getLogList(Pageable page, UserInfo userInfo, Long tenantsId, Long organizerId, String title, String beginTime, String endTime) {
        List<Map<String, Object>> list = logInfoRepository.getLogList(userInfo, tenantsId, organizerId, title, beginTime, endTime, page.getPageNumber(), page.getPageSize());
        int count = logInfoRepository.getLogListCount(userInfo, tenantsId, organizerId, title, beginTime, endTime);
        return RestfulRetUtils.getRetSuccessWithPage(list, count);
    }
}
