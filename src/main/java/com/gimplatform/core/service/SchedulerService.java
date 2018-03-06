package com.gimplatform.core.service;

import org.quartz.JobExecutionContext;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.entity.scheduler.JobInfo;
import com.gimplatform.core.entity.scheduler.ProcInfo;
import com.gimplatform.core.entity.scheduler.QrtzFiredDetails;
import com.gimplatform.core.entity.scheduler.RestfulInfo;

public interface SchedulerService {

    /**
     * 保存存储过程任务
     * @param userInfo
     * @param procInfo
     * @return
     */
    public JSONObject saveProcJob(UserInfo userInfo, ProcInfo procInfo);

    /**
     * 保存Restful任务
     * @param userInfo
     * @param restfulInfo
     * @return
     */
    public JSONObject saveRestfulJob(UserInfo userInfo, RestfulInfo restfulInfo);

    /**
     * 保存自定义任务
     * @param userInfo
     * @param jobInfo
     * @return
     */
    public JSONObject saveCustomJob(UserInfo userInfo, JobInfo jobInfo);

    /**
     * 获取任务历史
     * @param page
     * @param qrtzFiredDetails
     * @return
     */
    public JSONObject getJobHistory(Pageable page, QrtzFiredDetails qrtzFiredDetails);

    /**
     * 保存任务调度记录
     * @param context
     * @param oper
     */
    public void saveJobHistory(JobExecutionContext context, String oper);

    /**
     * 获取存储过程参数
     * @param proc
     * @return
     */
    public JSONObject getProcParams(ProcInfo proc);
}
