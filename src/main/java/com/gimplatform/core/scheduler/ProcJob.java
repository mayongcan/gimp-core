package com.gimplatform.core.scheduler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.SchedulerConstants;
import com.gimplatform.core.utils.DateUtils;
import com.gimplatform.core.utils.SchedulerUtils;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ProcJob implements Job {

	private static final Logger logger = LogManager.getLogger(ProcJob.class);

	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		Connection conn = null;
		CallableStatement proc = null;
		JobDetail jobDetail = null;

		try {
			jobDetail = jobContext.getJobDetail();
			JSONObject procJson = JSONObject.parseObject(jobDetail.getJobDataMap().getString("jobDetail"));
			String dbType = procJson.getString("dbType");
			String dbUrl = procJson.getString("dbUrl");
			String dbUser = procJson.getString("dbUser");
			String dbPwd = procJson.getString("dbPwd");
			String procName = procJson.getString("procName");
			JSONArray procParams = procJson.getJSONArray("procParams");
			String dbDriver = "";
			if (SchedulerConstants.DB_TYPE_MYCAT.equalsIgnoreCase(dbType) || SchedulerConstants.DB_TYPE_MYSQL.equalsIgnoreCase(dbType)) {
				dbDriver = SchedulerConstants.DB_DRIVER_MYSQL;
			} else if (SchedulerConstants.DB_TYPE_ORACLE.equalsIgnoreCase(dbType)) {
				dbDriver = SchedulerConstants.DB_DRIVER_ORACLE;
			} else if (SchedulerConstants.DB_TYPE_SQLSERVER.equalsIgnoreCase(dbType)) {
				dbDriver = SchedulerConstants.DB_DRIVER_SQLSERVER;
			}
			Class.forName(dbDriver);
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
			// 拼凑执行存储过程SQL语句
			StringBuilder params = new StringBuilder();
			for (int i = 0; i < procParams.size(); i++) {
				if (params.length() > 0) params.append(",");
				params.append("?");
			}
			String sql = "{ call " + procName + "(";
			if (params.length() > 0) sql += params.toString();
			sql += ")}";
			proc = conn.prepareCall(sql);
			for (int i = 0; i < procParams.size(); i++) {
				JSONObject jParam = procParams.getJSONObject(i);
				setProcParam(proc, jParam);
			}
			proc.execute();
			jobContext.getJobDetail().getJobDataMap().put("FIRE_RESULT", "执行成功");
		} catch (Exception e) {
			logger.error("执行存储过程失败", e);
			jobContext.getJobDetail().getJobDataMap().put("FIRE_RESULT", "执行失败");
		} finally {
			if (proc != null)
				try {
					proc.close();
				} catch (SQLException e) {
					logger.error("关闭存储过程对象失败", e);
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("关闭数据库连接对象失败", e);
				}
		}
	}

	private void setProcParam(CallableStatement proc, JSONObject jParam) throws SQLException {
		int index = jParam.getIntValue("paramIndex");
		String type = "," + jParam.getString("paramType") + ",";
		String paramVal = jParam.getString("paramValue");
		if (paramVal.startsWith("${"))
			paramVal = SchedulerUtils.getParamValByReg(paramVal);
		if (SchedulerConstants.PARAM_TYPE_INT.contains(type)) {
			if ("IN".equals(jParam.getString("paramMode"))) {
				proc.setInt(index, jParam.getIntValue("paramValue"));
			} else {
				proc.registerOutParameter(index, java.sql.Types.INTEGER);
			}
		} else if (SchedulerConstants.PARAM_TYPE_FLOAT.contains(type)) {
			if ("IN".equals(jParam.getString("paramMode"))) {
				proc.setFloat(index, jParam.getFloatValue("paramValue"));
			} else {
				proc.registerOutParameter(index, java.sql.Types.FLOAT);
			}
		} else if (SchedulerConstants.PARAM_TYPE_STRING.contains(type)) {
			if ("IN".equals(jParam.getString("paramMode"))) {
				proc.setString(index, paramVal);
			} else {
				proc.registerOutParameter(index, java.sql.Types.VARCHAR);
			}
		} else if (SchedulerConstants.PARAM_TYPE_DATE.contains(type)) {
			if ("IN".equals(jParam.getString("paramMode"))) {
				Long time = DateUtils.parseDate(paramVal).getTime();
				proc.setTimestamp(index, new Timestamp(time));
			} else {
				proc.registerOutParameter(index, java.sql.Types.TIMESTAMP);
			}
		} else {
			if ("IN".equals(jParam.getString("paramMode"))) {
				proc.setString(index, paramVal);
			} else {
				proc.registerOutParameter(index, java.sql.Types.VARCHAR);
			}
		}
	}

}
