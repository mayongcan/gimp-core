package com.gimplatform.core.entity.scheduler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class TriggerInfo extends JobInfo{
	
	private String triggerName;
	
	private String triggerGroup;
	
	private String triggerDesc;
	
	private String triggerType;
	
	private String triggerValue;
}
