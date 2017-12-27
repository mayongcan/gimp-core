/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;
import org.springframework.data.repository.NoRepositoryBean;
import com.gimplatform.core.entity.MessageUser;
import com.gimplatform.core.entity.UserInfo;

/**
 * 自定义实体资源类接口
 * @version 1.0
 * @author
 *
 */
@NoRepositoryBean
public interface MessageUserRepositoryCustom {

	/**
	 * 获取MessageUser列表
	 * @param messageUser
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getList(MessageUser messageUser, Map<String, Object> params, int pageIndex, int pageSize);
	
	/**
	 * 获取MessageUser列表总数
	 * @param messageUser
	 * @param params
	 * @return
	 */
	public int getListCount(MessageUser messageUser, Map<String, Object> params);
	
	/**
	 * 获取未读消息数
	 * @param userInfo
	 * @return
	 */
	public int getUnReadMessageCount(UserInfo userInfo);
}