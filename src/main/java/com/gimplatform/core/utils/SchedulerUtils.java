package com.gimplatform.core.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.SchedulerConstants;
import com.gimplatform.core.entity.scheduler.JobInfo;
import com.gimplatform.core.entity.scheduler.TriggerInfo;

/**
 * 任务调度工具类
 * 
 * @author zzd
 *
 */
public class SchedulerUtils {

	private static final Logger logger = LogManager.getLogger(SchedulerUtils.class);

	private static SchedulerFactoryBean schedulerFactoryBean = SpringContextHolder.getBean(SchedulerFactoryBean.class);;

	private static Scheduler scheduler = null;

	public static synchronized void startScheduler() {
		try {
			if (schedulerFactoryBean == null) {
				logger.error("启动任务调度服务失败:SchedulerFactoryBean注入失败");
				return;
			}
			scheduler = schedulerFactoryBean.getScheduler();
			scheduler.start();
			logger.info("启动任务调度服务");
		} catch (SchedulerException e) {
			logger.error("启动任务调度服务失败", e);
		}
	}

	public static void stopScheduler() {
		try {
			if (scheduler != null && scheduler.isStarted())
				scheduler.shutdown(true);
		} catch (SchedulerException e) {
			logger.error("关闭任务调度服务失败", e);
		}
	}

	public static JSONObject jobList() {
		try {
			List<String> allGroupList = scheduler.getJobGroupNames();
			List<JSONObject> listData = new ArrayList<>();
			JSONObject jsonJob = null;
			for (String group : allGroupList) {
				Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group));
				for (JobKey key : keys) {
					JobDetail jobDetail = scheduler.getJobDetail(key);
					jsonJob = new JSONObject();
					jsonJob.put("jobName", key.getName());
					jsonJob.put("jobGroup", key.getGroup());
					jsonJob.put("jobDescription", jobDetail.getDescription());
					jsonJob.put("jobData", jobDetail.getJobDataMap());
					@SuppressWarnings("unchecked")
					List<Trigger> triggerList = (List<Trigger>) scheduler.getTriggersOfJob(key);
					jsonJob.put("jobStatus", getJobStatus(triggerList, key));
					jsonJob.put("jobClassName", jobDetail.getJobClass().getName());
//					if ("自定义".equals(key.getGroup())) {
//						jsonJob.put("jobClassName", jobDetail.getJobClass().getName());
//					}
					listData.add(jsonJob);
				}
			}
			return RestfulRetUtils.getRetSuccessWithPage(listData, listData.size());
		} catch (SchedulerException e) {
			logger.error("获取任务列表失败", e);
			return RestfulRetUtils.getErrorMsg("444444", "获取任务列表失败");
		}
	}

	public static boolean isJobExsit(String jobName, String jobGroup) {
		if (scheduler == null)
			return true;
		try {
			return scheduler.checkExists(new JobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			logger.error("检查Job存在失败", e);
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean addJob(JobInfo jobInfo) {
		if (jobInfo == null) {
			return false;
		}
		try {
			String className = jobInfo.getJobClassName();
			Class<? extends Job> c = (Class<? extends Job>) Class.forName(className);
			JobDetail jobDetail = JobBuilder
					.newJob(c)
					.withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
					.withDescription(jobInfo.getJobDescription()).storeDurably(true).build();
			JobDataMap jobMap = new JobDataMap();
			jobMap.put("jobType", jobInfo.getJobType());
			jobMap.put("jobTypeDesc", jobInfo.getJobTypeDesc());
			jobMap.put("jobDetail", jobInfo.getJobData().toJSONString());
			jobDetail.getJobDataMap().putAll(jobMap);
			SchedulerUtils.scheduler.addJob(jobDetail, true);
			return true;
		} catch (Exception e) {
			logger.error("添加Job失败", e);
			return false;
		}
	}

	public static boolean deleteJob(String jobName, String jobGroup) {
		try {
			JobKey jobKey = new JobKey(jobName, jobGroup);
			return SchedulerUtils.scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			logger.error("删除Job失败", e);
			return false;
		}
	}

	public static boolean fireJob(String jobName, String jobGroup) {
		try {
			scheduler.triggerJob(new JobKey(jobName, jobGroup));
			return true;
		} catch (SchedulerException e) {
			logger.error("执行Job失败", e);
			return false;
		}
	}

	public static boolean pauseJob(String jobName, String jobGroup) {
		try {
			scheduler.pauseJob(new JobKey(jobName, jobGroup));
			return true;
		} catch (SchedulerException e) {
			logger.error("暂停Job失败", e);
			return false;
		}
	}

	public static boolean resumeJob(String jobName, String jobGroup) {
		try {
			scheduler.resumeJob(new JobKey(jobName, jobGroup));
			return true;
		} catch (SchedulerException e) {
			logger.error("恢复Job失败", e);
			return false;
		}
	}

	public static boolean isTriggerExsit(String triggerName, String triggerGroup) {
		if (scheduler == null)
			return true;
		try {
			return scheduler.checkExists(new TriggerKey(triggerName, triggerGroup));
		} catch (SchedulerException e) {
			logger.error("检查Trigger存在失败", e);
			return true;
		}
	}

	public static boolean addTrigger(TriggerInfo trigger) {
		try {
			if ("cron".equals(trigger.getTriggerType())) {
				scheduler.scheduleJob(buildCronTrigger(trigger));
			} else {
				scheduler.scheduleJob(buildSimpleTrigger(trigger));
			}
			return true;
		} catch (SchedulerException e) {
			logger.error("保存触发器失败", e);
			return false;
		}
	}

	public static boolean editTrigger(TriggerInfo trigger) {
		try {
			if ("cron".equals(trigger.getTriggerType())) {
				CronTrigger ctg = buildCronTrigger(trigger);
				scheduler.rescheduleJob(ctg.getKey(), ctg);
			} else {
				SimpleTrigger stg = buildSimpleTrigger(trigger);
				scheduler.rescheduleJob(stg.getKey(), stg);
			}
			return true;
		} catch (SchedulerException e) {
			logger.error("保存触发器失败", e);
			return false;
		}
	}

	public static boolean deleteTrigger(String triggerName, String triggerGroup) {
		try {
			scheduler.unscheduleJob(new TriggerKey(triggerName, triggerGroup));
			return true;
		} catch (SchedulerException e) {
			logger.error("删除触发器失败", e);
			return false;
		}
	}

	public static String getJobStatus(List<Trigger> triggerList, JobKey jobkey) {
		String status = "正常";
		try {
			if (triggerList.size() == 0)
				return "无触发器";
			for (Trigger trigger : triggerList) {
				TriggerState statusCode;
				statusCode = scheduler.getTriggerState(trigger.getKey());
				if (statusCode.equals(TriggerState.PAUSED)) {
					status = "暂停";
					scheduler.pauseJob(jobkey);
					return status;
				} else if (trigger.getKey().getName().startsWith("MT_")) {
					status = "手动触发";
				}
			}
		} catch (SchedulerException e) {
			logger.error("获取任务状态失败", e);
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getJobTriggers(String jobName, String jobGroup) {
		try {
			List<JSONObject> rows = new ArrayList<>();
			JSONObject jsonTg = null;
			List<Trigger> listTg = (List<Trigger>) scheduler.getTriggersOfJob(new JobKey(jobName, jobGroup));
			for (Trigger tg : listTg) {
				jsonTg = new JSONObject();
				jsonTg.put("triggerName", tg.getKey().getName());
				jsonTg.put("triggerGroup", tg.getKey().getGroup());
				jsonTg.put("triggerDesc", tg.getDescription());
				if (tg instanceof SimpleTrigger) {
					jsonTg.put("triggerType", "simple");
					jsonTg.put("triggerValue", ((SimpleTrigger) tg).getRepeatInterval() / 1000);
				} else {
					jsonTg.put("triggerType", "cron");
					jsonTg.put("triggerValue", ((CronTrigger) tg).getCronExpression());
				}
				rows.add(jsonTg);
			}
			return RestfulRetUtils.getRetSuccessWithPage(rows, rows.size());
		} catch (SchedulerException e) {
			logger.error("获取任务触发器列表失败", e);
			return RestfulRetUtils.getErrorMsg("444444", "获取任务触发器列表失败");
		}
	}

	private static SimpleTrigger buildSimpleTrigger(TriggerInfo trigger) {
		return TriggerBuilder
				.newTrigger()
				.withIdentity(trigger.getTriggerName(), trigger.getTriggerGroup())
				.forJob(trigger.getJobName(), trigger.getJobGroup())
				.withDescription(trigger.getTriggerDesc())
				.startAt(new Date())
				.withSchedule(SimpleScheduleBuilder
						.simpleSchedule()
						.withIntervalInSeconds(Integer.parseInt(trigger.getTriggerValue()))
						.repeatForever())
				.build();
	}

	private static CronTrigger buildCronTrigger(TriggerInfo trigger) {
		return TriggerBuilder
				.newTrigger()
				.withIdentity(trigger.getTriggerName(), trigger.getTriggerGroup())
				.forJob(trigger.getJobName(), trigger.getJobGroup())
				.withDescription(trigger.getTriggerDesc())
				.withSchedule(CronScheduleBuilder
						.cronSchedule(trigger.getTriggerValue()))
				.build();
	}

	public static String getDBTypeDesc(String dbType) {
		String type = "," + dbType + ",";
		if (SchedulerConstants.PARAM_TYPE_INT.contains(type)) {
			return SchedulerConstants.PARAM_TYPE_INT_DESC;
		} else if (SchedulerConstants.PARAM_TYPE_FLOAT.contains(type)) {
			return SchedulerConstants.PARAM_TYPE_FLOAT_DESC;
		} else if (SchedulerConstants.PARAM_TYPE_STRING.contains(type)) {
			return SchedulerConstants.PARAM_TYPE_STRING_DESC;
		} else if (SchedulerConstants.PARAM_TYPE_DATE.contains(type)) {
			return SchedulerConstants.PARAM_TYPE_DATE_DESC;
		} else {
			return dbType;
		}
	}

	public static String getParamValByReg(String reg) {
		if (reg.equals("${currDateSt}")) {
			return DateUtils.getDate() + " 00:00:00";
		} else if (reg.equals("${currDateEd}")) {
			return DateUtils.getDate() + " 23:59:59";
		} else if (reg.equals("${preDateSt}")) {
			Date date = DateUtils.addDays(new Date(), -1);
			return DateUtils.formatDate(date) + " 00:00:00";
		} else if (reg.equals("${preDateEd}")) {
			Date date = DateUtils.addDays(new Date(), -1);
			return DateUtils.formatDate(date) + " 23:59:59";
		} else {
			return reg;
		}
	}

	public static String replaceUrlParam(String url) {
		url = url.replaceAll("\\$\\{currDateSt\\}", getParamValByReg("${currDateSt}"));
		url = url.replaceAll("\\$\\{currDateEd\\}", getParamValByReg("${currDateEd}"));
		url = url.replaceAll("\\$\\{preDateSt\\}", getParamValByReg("${preDateSt}"));
		url = url.replaceAll("\\$\\{preDateEd\\}", getParamValByReg("${preDateEd}"));
		return url;
	}
}
