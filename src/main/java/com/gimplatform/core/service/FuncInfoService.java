package com.gimplatform.core.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.FuncInfo;
import com.gimplatform.core.entity.UserInfo;

/**
 * 权限服务类接口
 * @author zzd
 *
 */
public interface FuncInfoService {

	/**
	 * 加载权限数据到缓存
	 * @return
	 */
	public boolean loadFuncDataToCache();
	
	/**
	 * 根据用户ID获取用户权限
	 * @param userId
	 * @return
	 */
	public List<FuncInfo> getUserFunc(UserInfo userInfo);
	
	/**
	 * 通过权限树
	 * @return
	 */
	public JSONObject getFuncTree();
	
	/**
	 * 通过funcId获取权限树
	 * @param funcId
	 * @return
	 */
	public String getFuncTreeByFuncId(Long funcId);
	
	/**
	 * 根据租户ID获取权限树ID
	 * @param tenantsId
	 * @return
	 */
	public JSONObject getFuncIdByTenantsId(Long tenantsId);
	
	/**
	 * 根据租户ID获取权限树
	 * @param tenantsId
	 * @return
	 */
	public JSONObject getFuncTreeByTenantsId(Long tenantsId);
	
	/**
	 * 新增权限
	 * @param funcInfo
	 * @param userInfo
	 * @return
	 */
	public JSONObject addFunc(FuncInfo funcInfo, UserInfo userInfo);
	
	/**
	 * 编辑权限
	 * @param funcInfo
	 * @param userInfo
	 * @return
	 */
	public JSONObject editFunc(FuncInfo funcInfo, UserInfo userInfo);
	
	/**
	 * 删除权限
	 * @param idsList
	 * @param userInfo
	 * @return
	 */
	public JSONObject delFunc(String idsList, UserInfo userInfo);
	
	/**
	 * 保存导入的权限
	 * @param funcId
	 * @param json
	 * @param userInfo
	 * @return
	 */
	public JSONObject saveImportFunc(String funcId, JSONObject json, UserInfo userInfo);
	
	/**
	 * 获取json格式的树
	 * @param listFunc
	 * @param parentId
	 * @return
	 */
	public JSONArray getJsonTree(List<Map<String, Object>> listFunc, String parentId);
}
