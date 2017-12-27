package com.gimplatform.core.scheduler;

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
import com.gimplatform.core.utils.HttpUtils;
import com.gimplatform.core.utils.JsonUtils;
import com.gimplatform.core.utils.SchedulerUtils;
import com.gimplatform.core.utils.StringUtils;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RestfulJob implements Job {

	private static final Logger logger = LogManager.getLogger(RestfulJob.class);

	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		JobDetail jobDetail = jobContext.getJobDetail();
		JSONObject restJson = JSONObject.parseObject(jobDetail.getJobDataMap().getString("jobDetail"));
		String restUrl = restJson.getString("restUrl");
		String restType = restJson.getString("restType");
		JSONArray restParams = restJson.getJSONArray("restParams");
		JSONObject jParam = null;
		JSONObject json = new JSONObject();
		for (int i = 0; i < restParams.size(); i++) {
			jParam = restParams.getJSONObject(i);
			String paramVal = jParam.getString("paramValue");
			if (paramVal.startsWith("${"))
				paramVal = SchedulerUtils.getParamValByReg(paramVal);
			json.put(jParam.getString("paramName"), paramVal);
		}
		restUrl = SchedulerUtils.replaceUrlParam(restUrl);
		String result = "";
		String code = "";
		if ("GET".equals(restType)) {
			result = HttpUtils.get(restUrl);
		} else {
			result = HttpUtils.post(restUrl, JsonUtils.jsonToMap(json.toJSONString()));
		}
		if (!StringUtils.isBlank(result)) {
			json = JSONObject.parseObject(result);
			if(json != null) code = json.getString("RetCode");
		}
		if ("000000".equals(code)) {
			jobContext.getJobDetail().getJobDataMap().put("FIRE_RESULT", "调用接口成功");
		} else {
			jobContext.getJobDetail().getJobDataMap().put("FIRE_RESULT", "调用接口失败(" + code + ")");
			logger.error(json.get("content"));
		}
	}

}
