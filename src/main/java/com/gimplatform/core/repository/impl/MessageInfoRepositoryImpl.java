/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import com.gimplatform.core.utils.StringUtils;
import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.entity.MessageInfo;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.MessageInfoRepositoryCustom;

public class MessageInfoRepositoryImpl extends BaseRepository implements MessageInfoRepositoryCustom{

	private static final String MYSQL_SQL_GET_LIST = "SELECT tb.MSG_ID as msgId, tb.TENANTS_ID as tenantsId, tb.MSG_TITLE as msgTitle, tb.MSG_CONTENT as msgContent, tb.MSG_TYPE as msgType, "
	                + "tb.IS_REVOKE as isRevoke, tb.MSG_IMG as msgImg, tb.MSG_FILE as msgFile, tb.SEND_DATE as sendDate, tb.CREATE_BY as createBy, tb.CREATE_DATE as createDate, "
					+ "u.user_code as createUserCode, u.user_name as createUserName, "
					+ "(select group_concat(sysuser.user_id) from sys_message_user mu left join sys_user_info sysuser on sysuser.USER_ID = mu.USER_ID where mu.MSG_ID = tb.MSG_ID ) as userIdList, "
					+ "(select group_concat(sysuser.user_name) from sys_message_user mu left join sys_user_info sysuser on sysuser.USER_ID = mu.USER_ID where mu.MSG_ID = tb.MSG_ID ) as userNameList, "
					+ "(select group_concat(sysuser.user_code) from sys_message_user mu left join sys_user_info sysuser on sysuser.USER_ID = mu.USER_ID where mu.MSG_ID = tb.MSG_ID ) as userCodeList "
			+ "FROM sys_message_info tb left join sys_user_info u on u.user_id = tb.create_by "
			+ "WHERE 1 = 1 AND tb.IS_VALID = 'Y'";

	private static final String MYSQL_SQL_GET_LIST_COUNT = "SELECT count(1) as count "
			+ "FROM sys_message_info tb left join sys_user_info u on u.user_id = tb.create_by "
			+ "WHERE 1 = 1 AND tb.IS_VALID = 'Y'";

	private static final String ORACLE_SQL_GET_LIST = "SELECT tb.MSG_ID as \"msgId\", tb.TENANTS_ID as \"tenantsId\", tb.MSG_TITLE as \"msgTitle\", tb.MSG_CONTENT as \"msgContent\", tb.IS_REVOKE as \"isRevoke\", "
	                + "tb.MSG_TYPE as \"msgType\", tb.MSG_IMG as \"msgImg\", tb.MSG_FILE as \"msgFile\", tb.SEND_DATE as \"sendDate\", tb.CREATE_BY as \"createBy\", tb.CREATE_DATE as \"createDate\", "
					+ "u.user_code as \"createUserCode\",  u.user_name as \"createUserName\", "
					+ "(select wm_concat(sysuser.user_id) from sys_message_user mu left join sys_user_info sysuser on sysuser.USER_ID = mu.USER_ID where mu.MSG_ID = tb.MSG_ID ) as \"userIdList\", "
					+ "(select wm_concat(sysuser.user_name) from sys_message_user mu left join sys_user_info sysuser on sysuser.USER_ID = mu.USER_ID where mu.MSG_ID = tb.MSG_ID ) as \"userNameList\", "
					+ "(select wm_concat(sysuser.user_code) from sys_message_user mu left join sys_user_info sysuser on sysuser.USER_ID = mu.USER_ID where mu.MSG_ID = tb.MSG_ID ) as \"userCodeList\" "
			+ "FROM sys_message_info tb left join sys_user_info u on u.user_id = tb.create_by "
			+ "WHERE 1 = 1 AND tb.IS_VALID = 'Y'";

	private static final String ORACLE_SQL_GET_LIST_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM sys_message_info tb left join sys_user_info u on u.user_id = tb.create_by "
			+ "WHERE 1 = 1 AND tb.IS_VALID = 'Y'";

	public MessageInfoRepositoryImpl(){
		sqlMap = new HashMap<String, String>();
		sqlMap.put("MYSQL_SQL_GET_LIST", MYSQL_SQL_GET_LIST);
		sqlMap.put("MYSQL_SQL_GET_LIST_COUNT", MYSQL_SQL_GET_LIST_COUNT);
		sqlMap.put("ORACLE_SQL_GET_LIST", ORACLE_SQL_GET_LIST);
		sqlMap.put("ORACLE_SQL_GET_LIST_COUNT", ORACLE_SQL_GET_LIST_COUNT);
	}
	
	public List<Map<String, Object>> getList(MessageInfo messageInfo, Map<String, Object> params, int pageIndex, int pageSize) {
		//生成查询条件
		SqlParams sqlParams = genListWhere(getSqlContent("SQL_GET_LIST"), messageInfo, params);
		//添加分页和排序
		sqlParams = getPageableSql(sqlParams, pageIndex, pageSize, " MSG_ID DESC ", " \"msgId\" DESC ");
		return getResultList(sqlParams);
	}

	public int getListCount(MessageInfo messageInfo, Map<String, Object> params) {
		//生成查询条件
		SqlParams sqlParams = genListWhere(getSqlContent("SQL_GET_LIST_COUNT"), messageInfo, params);
		return getResultListTotalCount(sqlParams);
	}
	
	/**
	 * 生成查询条件
	 * @param sql
	 * @param params
	 * @return
	 */
	private SqlParams genListWhere(String sql, MessageInfo messageInfo, Map<String, Object> params){
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(sql);
		//添加查询参数
		if (messageInfo != null && !StringUtils.isBlank(messageInfo.getMsgTitle())) {
            sqlParams.querySql.append(getLikeSql("tb.MSG_TITLE", ":msgTitle"));
			sqlParams.paramsList.add("msgTitle");
			sqlParams.valueList.add(messageInfo.getMsgTitle());
		}
		if (messageInfo != null && !StringUtils.isBlank(messageInfo.getMsgType())) {
			sqlParams.querySql.append(" AND tb.MSG_TYPE = :msgType ");
			sqlParams.paramsList.add("msgType");
			sqlParams.valueList.add(messageInfo.getMsgType());
		}
        if (messageInfo != null && messageInfo.getTenantsId() != null) {
            sqlParams.querySql.append(" AND tb.TENANTS_ID = :tenantsId ");
            sqlParams.paramsList.add("tenantsId");
            sqlParams.valueList.add(messageInfo.getTenantsId());
        }
        if (messageInfo != null && !StringUtils.isBlank(messageInfo.getIsRevoke())) {
            sqlParams.querySql.append(" AND tb.IS_REVOKE = :isRevoke ");
            sqlParams.paramsList.add("isRevoke");
            sqlParams.valueList.add(messageInfo.getIsRevoke());
        }
        String beginCreateDate = MapUtils.getString(params, "beginCreateDate");
        String endCreateDate = MapUtils.getString(params, "endCreateDate");
        if(!StringUtils.isBlank(beginCreateDate) && !StringUtils.isBlank(endCreateDate)) {
            sqlParams.querySql.append(" AND tb.CREATE_DATE between :beginCreateDate and :endCreateDate ");
            sqlParams.paramsList.add("beginCreateDate");
            sqlParams.paramsList.add("endCreateDate");
            sqlParams.valueList.add(beginCreateDate);
            sqlParams.valueList.add(endCreateDate);
        }
        String beginSendDate = MapUtils.getString(params, "beginSendDate");
        String endSendDate = MapUtils.getString(params, "endSendDate");
        if(!StringUtils.isBlank(beginSendDate) && !StringUtils.isBlank(endSendDate)) {
            sqlParams.querySql.append(" AND tb.SEND_DATE between :beginSendDate and :endSendDate ");
            sqlParams.paramsList.add("beginSendDate");
            sqlParams.paramsList.add("endSendDate");
            sqlParams.valueList.add(beginSendDate);
            sqlParams.valueList.add(endSendDate);
        }
        return sqlParams;
	}
}