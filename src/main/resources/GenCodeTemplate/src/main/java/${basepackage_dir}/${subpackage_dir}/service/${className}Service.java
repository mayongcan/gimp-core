<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>
<#assign shortName = table.shortName>
package ${basepackage}.${subpackage}.service;

<#if pageType = "2">
import java.util.List;
</#if>
import java.util.Map;

import org.springframework.data.domain.Pageable;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.UserInfo;

import ${basepackage}.${subpackage}.entity.${className};

/**
 * 服务类接口
 * @version 1.0
 * @author 
 */
public interface ${className}Service {
	
	/**
	 * 获取列表
	 * @param page
	 * @param ${classNameLower}
	 * @return
	 */
	public JSONObject getList(Pageable page, ${className} ${classNameLower}, Map<String, Object> params);
	
	/**
	 * 新增
	 * @param params
	 * @param userInfo
	 * @return
	 */
	public JSONObject add(Map<String, Object> params, UserInfo userInfo);
	
	/**
	 * 编辑
	 * @param params
	 * @param userInfo
	 * @return
	 */
	public JSONObject edit(Map<String, Object> params, UserInfo userInfo);
	
	/**
	 * 删除
	 * @param idsList
	 * @param userInfo
	 * @return
	 */
	public JSONObject del(String idsList, UserInfo userInfo);
	
	<#-- 输出树列表的数据库操作 -->
	<#if pageType = "2" >
	/**
	 * 获取树内容
	 * @return
	 */
	public JSONObject getTreeList();

	/**
	 * 根据父标志，获取列表
	 * @param ${classNameLower}
	 * @return
	 */
	public List<${className}> getListByParentIds(${className} ${classNameLower});
	</#if>

}
