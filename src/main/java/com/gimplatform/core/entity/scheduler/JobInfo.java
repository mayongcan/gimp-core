package com.gimplatform.core.entity.scheduler;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobInfo {
	
	private String jobName;
	
	private String jobGroup;
	
	private String jobDescription;
	
	private String jobClassName;
	
	private String jobType;
	
	private String jobTypeDesc;
	
	private JSONObject jobData;
	
	private String operType;
	
	private List<?> params;
}
