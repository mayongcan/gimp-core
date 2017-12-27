/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;
import org.springframework.data.repository.NoRepositoryBean;
import com.gimplatform.core.entity.MessageInfo;

/**
 * 自定义实体资源类接口
 * @version 1.0
 * @author
 *
 */
@NoRepositoryBean
public interface MessageInfoRepositoryCustom {

	/**
	 * 获取MessageInfo列表
	 * @param messageInfo
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getList(MessageInfo messageInfo, Map<String, Object> params, int pageIndex, int pageSize);
	
	/**
	 * 获取MessageInfo列表总数
	 * @param messageInfo
	 * @param params
	 * @return
	 */
	public int getListCount(MessageInfo messageInfo, Map<String, Object> params);
}