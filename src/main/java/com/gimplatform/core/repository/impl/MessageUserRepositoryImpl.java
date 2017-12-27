/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.repository.impl;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.entity.MessageUser;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.MessageUserRepositoryCustom;
import com.gimplatform.core.utils.StringUtils;

public class MessageUserRepositoryImpl extends BaseRepository implements MessageUserRepositoryCustom{

	private static final String SQL_GET_LIST = "SELECT tb.ID as \"id\", tb.MSG_ID as \"msgId\", tb.USER_ID as \"userId\", "
					+ "tb.IS_SEND as \"isSend\", tb.IS_READ as \"isRead\", tb.READ_DATE as \"readDate\", tb.SEND_DATE as \"sendDate\", "
					+ "m.MSG_TITLE AS \"msgTitle\", m.MSG_CONTENT AS \"msgContent\", m.MSG_TYPE AS \"msgType\", m.MSG_IMG AS \"msgImg\", m.MSG_FILE AS \"msgFile\","
					+ "u.user_code as \"createUserCode\", u.user_name as \"createUserName\" "
			+ "FROM sys_message_user tb left join sys_message_info m on tb.MSG_ID = m.MSG_ID "
					+ "left join sys_user_info u on m.CREATE_BY = u.USER_ID "
			+ "WHERE 1 = 1 ";

	private static final String SQL_GET_LIST_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM sys_message_user tb left join sys_message_info m on tb.MSG_ID = m.MSG_ID "
					+ "left join sys_user_info u on m.CREATE_BY = u.USER_ID "
			+ "WHERE 1 = 1 ";

	private static final String SQL_GET_UNREAD_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM sys_message_user tb "
			+ "WHERE tb.is_send = '1' and tb.is_read = '0' ";
	
	public List<Map<String, Object>> getList(MessageUser messageUser, Map<String, Object> params, int pageIndex, int pageSize) {
		//生成查询条件
		SqlParams sqlParams = genListWhere(SQL_GET_LIST, messageUser, params);
		sqlParams = getPageableSql(sqlParams, pageIndex, pageSize, " ID DESC ", " \"id\" DESC ");
		return getResultList(sqlParams);
	}

	public int getListCount(MessageUser messageUser, Map<String, Object> params) {
		//生成查询条件
		SqlParams sqlParams = genListWhere(SQL_GET_LIST_COUNT, messageUser, params);
		return getResultListTotalCount(sqlParams);
	}
	
	/**
	 * 生成查询条件
	 * @param sql
	 * @param messageUser
	 * @param params
	 * @return
	 */
	private SqlParams genListWhere(String sql, MessageUser messageUser, Map<String, Object> params){
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(sql);
		String msgTitle = MapUtils.getString(params, "msgTitle");
		String msgType = MapUtils.getString(params, "msgType");
		//添加查询参数
		if (messageUser != null && messageUser.getUserId() != null) {
			sqlParams.querySql.append(" AND tb.USER_ID = :userId ");
			sqlParams.paramsList.add("userId");
			sqlParams.valueList.add(messageUser.getUserId());
		}
		if (messageUser != null && StringUtils.isNotBlank(messageUser.getIsSend())) {
			sqlParams.querySql.append(" AND tb.IS_SEND = :isSend ");
			sqlParams.paramsList.add("isSend");
			sqlParams.valueList.add(messageUser.getIsSend());
		}
		if (messageUser != null && StringUtils.isNotBlank(messageUser.getIsRead())) {
			sqlParams.querySql.append(" AND tb.IS_READ = :isRead ");
			sqlParams.paramsList.add("isRead");
			sqlParams.valueList.add(messageUser.getIsRead());
		}
		if (StringUtils.isNotBlank(msgTitle)) {
			sqlParams.querySql.append(" AND m.MSG_TITLE like concat('%', :msgTitle,'%') ");
			sqlParams.paramsList.add("msgTitle");
			sqlParams.valueList.add(msgTitle);
		}
		if (StringUtils.isNotBlank(msgType)) {
			sqlParams.querySql.append(" AND m.MSG_TYPE = :msgType ");
			sqlParams.paramsList.add("msgType");
			sqlParams.valueList.add(msgType);
		}
        return sqlParams;
	}

	public int getUnReadMessageCount(UserInfo userInfo) {
		//组合查询条件
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(SQL_GET_UNREAD_COUNT);
		if (userInfo != null && userInfo.getUserId() != null) {
			sqlParams.querySql.append(" AND tb.USER_ID = :userId ");
			sqlParams.paramsList.add("userId");
			sqlParams.valueList.add(userInfo.getUserId());
		}
		return getResultListTotalCount(sqlParams);
	}
}