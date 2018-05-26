package com.gimplatform.core.repository.impl;

import java.util.List;
import java.util.Map;
import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.LogInfoRepositoryCustom;
import com.gimplatform.core.utils.StringUtils;

public class LogInfoRepositoryImpl extends BaseRepository implements LogInfoRepositoryCustom{
	
	private static final String SQL_LOG_LIST = "SELECT log.LOG_ID as \"logId\", log.LOG_TYPE as \"logType\", log.LOG_TITLE as \"logTitle\", log.CREATE_DATE as \"createDate\", " 
					+ "log.REMOTE_ADDR as \"remoteAddr\", log.USER_AGENT as \"userAgent\", log.REQUEST_URI as \"requestUri\", log.METHOD as \"method\", log.PARAMS as \"params\", "
					+ "log.EXCEPTION as \"exception\", user.USER_CODE as \"userCode\", org.ORGANIZER_NAME as \"organizerName\", ten.TENANTS_NAME as \"tenantsName\" "
			+ "FROM sys_log_info log left join sys_user_info user on log.CREATE_BY = user.USER_ID "
					+ "left join sys_organizer_info org on org.ORGANIZER_ID = user.ORGANIZER_ID "
					+ "left join sys_tenants_info ten on ten.TENANTS_ID = user.TENANTS_ID "
			+ "WHERE 1 = 1 ";
	
	private static final String SQL_LOG_LIST_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM sys_log_info log left join sys_user_info user on log.CREATE_BY = user.USER_ID "
					+ "left join sys_organizer_info org on org.ORGANIZER_ID = user.ORGANIZER_ID "
					+ "left join sys_tenants_info ten on ten.TENANTS_ID = user.TENANTS_ID "
			+ "WHERE 1 = 1 ";

	public List<Map<String, Object>> getLogList(UserInfo userInfo, Long tenantsId, Long organizerId, String title, String beginTime, String endTime, int pageIndex, int pageSize) {
		//生成查询条件
		SqlParams sqlParams = genLogListWhere(SQL_LOG_LIST, userInfo, tenantsId, organizerId, title, beginTime, endTime);
		sqlParams = getPageableSql(sqlParams, pageIndex, pageSize, " LOG_ID DESC ", " \"logId\" DESC ");
		return getResultList(sqlParams);
	}

	public int getLogListCount(UserInfo userInfo, Long tenantsId, Long organizerId, String title, String beginTime, String endTime) {
		//生成查询条件
		SqlParams sqlParams = genLogListWhere(SQL_LOG_LIST_COUNT, userInfo, tenantsId, organizerId, title, beginTime, endTime);
		return getResultListTotalCount(sqlParams);
	}
	
	/**
	 * 生成查询条件
	 * @param sql
	 * @return
	 */
	private SqlParams genLogListWhere(String sql, UserInfo userInfo, Long tenantsId, Long organizerId, String title, String beginTime, String endTime){
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(sql);
		//添加查询参数
		if(!StringUtils.isBlank(title)) {
            sqlParams.querySql.append(getLikeSql("log.LOG_TITLE", ":title"));
			sqlParams.paramsList.add("title");
			sqlParams.valueList.add(title);
        }
		if(tenantsId != null){
			sqlParams.querySql.append(" AND ten.TENANTS_ID =:tenantsId ");
			sqlParams.paramsList.add("tenantsId");
			sqlParams.valueList.add(tenantsId);
		}
		if(organizerId != null){
			sqlParams.querySql.append(" AND org.ORGANIZER_ID =:organizerId ");
			sqlParams.paramsList.add("organizerId");
			sqlParams.valueList.add(organizerId);
		}
		//非管理员只能查看自己的日志
		if(userInfo != null && userInfo.getIsAdmin().equals("N")){
			sqlParams.querySql.append(" AND user.USER_ID =:userId ");
			sqlParams.paramsList.add("userId");
			sqlParams.valueList.add(userInfo.getUserId());
		}
        if(!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
        	sqlParams.querySql.append(" AND log.CREATE_DATE between :beginTime and :endTime ");
        	sqlParams.paramsList.add("beginTime");
        	sqlParams.paramsList.add("endTime");
        	sqlParams.valueList.add(beginTime);
        	sqlParams.valueList.add(endTime);
        }
        return sqlParams;
	}

}
