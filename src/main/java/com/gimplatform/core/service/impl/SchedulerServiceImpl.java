package com.gimplatform.core.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.SchedulerConstants;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.entity.scheduler.JobInfo;
import com.gimplatform.core.entity.scheduler.ProcInfo;
import com.gimplatform.core.entity.scheduler.QrtzFiredDetails;
import com.gimplatform.core.entity.scheduler.RestfulInfo;
import com.gimplatform.core.repository.QrtzFiredDetailsRepository;
import com.gimplatform.core.service.SchedulerService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SchedulerUtils;

/**
 * 任务调度
 * @author zzd
 */
@Service
public class SchedulerServiceImpl implements SchedulerService {

    private static final Logger logger = LogManager.getLogger(SchedulerServiceImpl.class);

    @Autowired
    private QrtzFiredDetailsRepository qrtzFiredDetailsRepository;

    @Override
    public JSONObject saveProcJob(UserInfo userInfo, ProcInfo procInfo) {
        JSONObject json = new JSONObject();
        JSONObject jobData = new JSONObject();
        jobData.put("dbType", procInfo.getDbType());
        jobData.put("dbUrl", procInfo.getDbUrl());
        jobData.put("dbUser", procInfo.getDbUser());
        jobData.put("dbPwd", procInfo.getDbPwd());
        jobData.put("dbName", procInfo.getDbName());
        jobData.put("procName", procInfo.getProcName());
        jobData.put("procParams", procInfo.getProcParams());
        procInfo.setJobData(jobData);
        procInfo.setJobClassName(SchedulerConstants.JOB_CLASS_PROC);
        procInfo.setJobTypeDesc(SchedulerConstants.JOB_TYPE_PROC_DESC);
        boolean rlt = SchedulerUtils.addJob(procInfo);
        if (rlt) {
            json = RestfulRetUtils.getRetSuccess();
        } else {
            json = RestfulRetUtils.getErrorMsg("42001", "保存任务失败");
        }
        return json;
    }

    @Override
    public JSONObject saveRestfulJob(UserInfo userInfo, RestfulInfo restfulInfo) {
        JSONObject json = new JSONObject();
        JSONObject jobData = new JSONObject();
        jobData.put("restType", restfulInfo.getRestType());
        jobData.put("restUrl", restfulInfo.getRestUrl());
        jobData.put("restParams", restfulInfo.getRestParams());
        restfulInfo.setJobData(jobData);
        restfulInfo.setJobClassName(SchedulerConstants.JOB_CLASS_RESTFUL);
        restfulInfo.setJobTypeDesc(SchedulerConstants.JOB_TYPE_RESTFUL_DESC);
        boolean rlt = SchedulerUtils.addJob(restfulInfo);
        if (rlt) {
            json = RestfulRetUtils.getRetSuccess();
        } else {
            json = RestfulRetUtils.getErrorMsg("42001", "保存任务失败");
        }
        return json;
    }

    @Override
    public JSONObject saveCustomJob(UserInfo userInfo, JobInfo jobInfo) {
        JSONObject json = new JSONObject();
        JSONObject jobData = new JSONObject();
        jobData.put("params", jobInfo.getParams());
        jobInfo.setJobData(jobData);
        jobInfo.setJobTypeDesc(SchedulerConstants.JOB_TYPE_CUSTOM_DESC);
        boolean rlt = SchedulerUtils.addJob(jobInfo);
        if (rlt) {
            json = RestfulRetUtils.getRetSuccess();
        } else {
            json = RestfulRetUtils.getErrorMsg("42001", "保存任务失败");
        }
        return json;
    }

    @Override
    public JSONObject getJobHistory(Pageable page, QrtzFiredDetails qrtzFiredDetails) {
        List<Map<String, Object>> list = qrtzFiredDetailsRepository.getList(qrtzFiredDetails, page.getPageNumber(), page.getPageSize());
        int count = qrtzFiredDetailsRepository.getListCount(qrtzFiredDetails);
        return RestfulRetUtils.getRetSuccessWithPage(list, count);
    }

    @Override
    public void saveJobHistory(JobExecutionContext context, String oper) {
        try {
            JobDetail jobDetail = context.getJobDetail();
            String jobName = jobDetail.getKey().getName();
            String jobGroup = jobDetail.getKey().getGroup();

            Trigger trigger = context.getTrigger();
            String triggerName = trigger.getKey().getName();
            String triggerGroup = trigger.getKey().getGroup();

            String triggerType = "";
            String jobTypeDesc = jobDetail.getJobDataMap().getString("jobTypeDesc");
            Date previousFireTime = trigger.getPreviousFireTime();
            Date nextFireTime = null;
            if (trigger instanceof SimpleTrigger) {
                SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
                triggerType = "简单触发器";
                nextFireTime = simpleTrigger.getNextFireTime();
            } else if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                triggerType = "Cron表达式";
                nextFireTime = cronTrigger.getNextFireTime();
            } else {
                triggerType = trigger.getKey().getName();
                logger.error("未可识别的触发器类型");
            }

            if ("add".equals(oper)) {
                logger.info("新增定时任务记录");
                Date startTime = new Date();
                String jobStatus = "执行中";
                QrtzFiredDetails firedDetail = new QrtzFiredDetails();
                firedDetail.setStartDate(startTime);
                firedDetail.setNextFiredDate(nextFireTime);
                firedDetail.setJobName(jobName);
                firedDetail.setJobGroup(jobGroup);
                firedDetail.setTriggerName(triggerName);
                firedDetail.setTriggerGroup(triggerGroup);
                firedDetail.setJobType(jobTypeDesc);
                firedDetail.setTriggerType(triggerType);
                firedDetail.setJobStatus(jobStatus);
                firedDetail.setFiredDate(previousFireTime);
                qrtzFiredDetailsRepository.save(firedDetail);
            } else {
                String firedResult = jobDetail.getJobDataMap().getString("FIRE_RESULT");
                logger.info("更新定时任务记录:" + firedResult);
                String jobStatus = "执行完毕";
                qrtzFiredDetailsRepository.updateDetails(new Date(), firedResult, jobStatus, jobName, jobGroup, triggerName, triggerGroup, "执行中", trigger.getPreviousFireTime());
            }
        } catch (Exception e) {
            logger.error("保存任务调度历史数据失败", e);
        }
    }

    @Override
    public JSONObject getProcParams(ProcInfo proc) {
        String dbType = proc.getDbType() == null ? "" : proc.getDbType();
        if (SchedulerConstants.DB_TYPE_MYCAT.equalsIgnoreCase(dbType)) {
            return getMySqlParams(proc);
        } else if (SchedulerConstants.DB_TYPE_MYSQL.equalsIgnoreCase(dbType)) {
            return getMySqlParams(proc);
        } else if (SchedulerConstants.DB_TYPE_ORACLE.equalsIgnoreCase(dbType)) {
            return getOracleParams(proc);
        } else if (SchedulerConstants.DB_TYPE_SQLSERVER.equalsIgnoreCase(dbType)) {
            return getSqlServerParams(proc);
        } else {
            logger.error("无法识别的数据库类型：" + proc.getDbType());
            return RestfulRetUtils.getErrorMsg("42001", "无法识别的数据库类型：" + proc.getDbType());
        }
    }

    private JSONObject getMySqlParams(ProcInfo proc) {
        JSONObject json = new JSONObject();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "select ordinal_position,parameter_name,data_type,parameter_mode from information_schema.parameters " + "where specific_schema=? and routine_type=? and specific_name=?";
            if (SchedulerConstants.DB_TYPE_MYCAT.equalsIgnoreCase(proc.getDbType())) {
                sql = SchedulerConstants.MYCAT_GBL_NOTESQL + sql;
            }
            Class.forName(SchedulerConstants.DB_DRIVER_MYSQL).newInstance();
            conn = DriverManager.getConnection(proc.getDbUrl(), proc.getDbUser(), proc.getDbPwd());
            ps = conn.prepareStatement(sql);
            ps.setString(1, proc.getDbName());
            ps.setString(2, "PROCEDURE");
            ps.setString(3, proc.getProcName());
            rs = ps.executeQuery();
            List<JSONObject> listParam = new ArrayList<>();
            JSONObject jsonParam = null;
            while (rs.next()) {
                jsonParam = new JSONObject();
                jsonParam.put("paramIndex", rs.getInt("ordinal_position"));
                jsonParam.put("paramName", rs.getString("parameter_name"));
                jsonParam.put("paramType", rs.getString("data_type"));
                jsonParam.put("paramTypeDesc", SchedulerUtils.getDBTypeDesc(rs.getString("data_type")) + "(" + rs.getString("parameter_mode") + ")");
                jsonParam.put("paramMode", rs.getString("parameter_mode"));
                listParam.add(jsonParam);
            }
            json = RestfulRetUtils.getRetSuccessWithPage(listParam, listParam.size());
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.error("获取" + proc.getDbType() + "存储过程（" + proc.getProcName() + "）参数失败", e);
            json = RestfulRetUtils.getErrorMsg("42001", "获取" + proc.getDbType() + "存储过程（" + proc.getProcName() + "）参数失败");
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("关闭结果集连接失败", e);
                }
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("关闭查询对象连接失败", e);
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("关闭数据库连接失败", e);
                }
        }
        return json;
    }

    private JSONObject getOracleParams(ProcInfo proc) {
        JSONObject json = new JSONObject();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "select position,argument_name,data_type,in_out from user_procedures up " + "inner join user_arguments ua on up.object_id=ua.object_id " + "where up.object_type=? and up.object_name=?";
            Class.forName(SchedulerConstants.DB_DRIVER_ORACLE).newInstance();
            conn = DriverManager.getConnection(proc.getDbUrl(), proc.getDbUser(), proc.getDbPwd());
            ps = conn.prepareStatement(sql);
            ps.setString(1, "PROCEDURE");
            ps.setString(2, proc.getProcName());
            rs = ps.executeQuery();
            List<JSONObject> listParam = new ArrayList<>();
            JSONObject jsonParam = null;
            while (rs.next()) {
                jsonParam = new JSONObject();
                jsonParam.put("paramIndex", rs.getInt("position"));
                jsonParam.put("paramName", rs.getString("argument_name"));
                jsonParam.put("paramType", rs.getString("data_type"));
                jsonParam.put("paramTypeDesc", SchedulerUtils.getDBTypeDesc(rs.getString("data_type")) + "(" + rs.getString("in_out") + ")");
                jsonParam.put("paramMode", rs.getString("in_out"));
                listParam.add(jsonParam);
            }
            json = RestfulRetUtils.getRetSuccessWithPage(listParam, listParam.size());
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.error("获取" + proc.getDbType() + "存储过程（" + proc.getProcName() + "）参数失败", e);
            json = RestfulRetUtils.getErrorMsg("42001", "获取" + proc.getDbType() + "存储过程（" + proc.getProcName() + "）参数失败");
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("关闭结果集连接失败", e);
                }
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("关闭查询对象连接失败", e);
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("关闭数据库连接失败", e);
                }
        }
        return json;
    }

    private JSONObject getSqlServerParams(ProcInfo proc) {
        JSONObject json = new JSONObject();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "select p.parameter_id,p.name as param_name,t.name as data_type,is_output from sys.all_objects o,sys.parameters p,sys.types t " + "where o.object_id=p.object_id and p.user_type_id = t.user_type_id and upper(o.name) =?";
            Class.forName(SchedulerConstants.DB_DRIVER_ORACLE).newInstance();
            conn = DriverManager.getConnection(proc.getDbUrl(), proc.getDbUser(), proc.getDbPwd());
            ps = conn.prepareStatement(sql);
            ps.setString(1, proc.getProcName().toUpperCase());
            rs = ps.executeQuery();
            List<JSONObject> listParam = new ArrayList<>();
            JSONObject jsonParam = null;
            while (rs.next()) {
                String inOut = rs.getInt("is_output") == 0 ? "IN" : "OUT";
                jsonParam = new JSONObject();
                jsonParam.put("paramIndex", rs.getInt("parameter_id"));
                jsonParam.put("paramName", rs.getString("param_name"));
                jsonParam.put("paramType", rs.getString("data_type"));
                jsonParam.put("paramTypeDesc", SchedulerUtils.getDBTypeDesc(rs.getString("data_type")) + "(" + inOut + ")");
                jsonParam.put("paramMode", inOut);
                listParam.add(jsonParam);
            }
            json = RestfulRetUtils.getRetSuccessWithPage(listParam, listParam.size());
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.error("获取" + proc.getDbType() + "存储过程（" + proc.getProcName() + "）参数失败", e);
            json = RestfulRetUtils.getErrorMsg("42001", "获取" + proc.getDbType() + "存储过程（" + proc.getProcName() + "）参数失败");
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("关闭结果集连接失败", e);
                }
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("关闭查询对象连接失败", e);
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("关闭数据库连接失败", e);
                }
        }
        return json;
    }
}
