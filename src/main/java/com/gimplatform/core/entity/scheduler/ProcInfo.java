package com.gimplatform.core.entity.scheduler;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class ProcInfo extends JobInfo{
	
	private String dbType;
	
	private String dbUrl;
	
	private String dbUser;
	
	private String dbPwd;
	
	private String dbName;
	
	private String procName;
	
	private List<?> procParams;
}
