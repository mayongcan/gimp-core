package com.gimplatform.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.AuditInfo;
import com.gimplatform.core.entity.UserInfo;

public interface AuditInfoService {

	/**
	 * 获取列表
	 * @param auditInfo
	 * @return
	 */
	public Page<AuditInfo> getList(Pageable page, AuditInfo auditInfo);

	/**
	 * 新增
	 * 
	 * @param auditInfo
	 * @param userInfo
	 * @return
	 */
	public JSONObject add(AuditInfo auditInfo, UserInfo userInfo);

	/**
	 * 编辑
	 * 
	 * @param auditInfo
	 * @param userInfo
	 * @return
	 */
	public JSONObject edit(AuditInfo auditInfo, UserInfo userInfo);

	/**
	 * 删除
	 * 
	 * @param idsList
	 * @param userInfo
	 * @return
	 */
	public JSONObject del(String idsList, UserInfo userInfo);
	

	public JSONObject auditUserPass(String idsList, UserInfo userInfo);

	public JSONObject auditNotPass(String idsList, UserInfo userInfo);
}
