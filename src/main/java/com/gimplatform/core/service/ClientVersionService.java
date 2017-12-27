package com.gimplatform.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.ClientVersion;
import com.gimplatform.core.entity.UserInfo;

public interface ClientVersionService {

	/**
	 * 获取客户端版本列表
	 * @param clientVersion
	 * @return
	 */
	public Page<ClientVersion> getList(Pageable page, ClientVersion clientVersion);

	/**
	 * 新增客户端版本规则
	 * 
	 * @param clientVersion
	 * @param userInfo
	 * @return
	 */
	public JSONObject add(ClientVersion clientVersion, UserInfo userInfo);

	/**
	 * 编辑客户端版本规则
	 * 
	 * @param clientVersion
	 * @param userInfo
	 * @return
	 */
	public JSONObject edit(ClientVersion clientVersion, UserInfo userInfo);

	/**
	 * 删除培训积分规则
	 * 
	 * @param idsList
	 * @param userInfo
	 * @return
	 */
	public JSONObject del(String idsList, UserInfo userInfo);
}
