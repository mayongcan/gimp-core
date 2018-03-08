<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>
<#assign shortName = table.shortName>
package ${basepackage}.${subpackage}.service.impl;

<#if pageType = "2">
import java.util.HashMap;
import com.alibaba.fastjson.JSONArray;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.tree.Tree;
import com.gimplatform.core.tree.TreeNode;
import com.gimplatform.core.tree.TreeNodeExtend;
</#if>
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

import ${basepackage}.${subpackage}.service.${className}Service;
import ${basepackage}.${subpackage}.entity.${className};
import ${basepackage}.${subpackage}.repository.${className}Repository;

@Service
public class ${className}ServiceImpl implements ${className}Service {
	
    @Autowired
    private ${className}Repository ${classNameLower}Repository;

	@Override
	public JSONObject getList(Pageable page, ${className} ${classNameLower}, Map<String, Object> params) {
		List<Map<String, Object>> list = ${classNameLower}Repository.getList(${classNameLower}, params, page.getPageNumber(), page.getPageSize());
		int count = ${classNameLower}Repository.getListCount(${classNameLower}, params);
		return RestfulRetUtils.getRetSuccessWithPage(list, count);	
	}

	@Override
	public JSONObject add(Map<String, Object> params, UserInfo userInfo) {
	    ${className} ${classNameLower} = (${className}) BeanUtils.mapToBean(params, ${className}.class);
		<#if table.hasIsValid>
		${classNameLower}.setIsValid(Constants.IS_VALID_VALID);
		</#if>
		<#if table.hasCreateBy>
		${classNameLower}.setCreateBy(userInfo.getUserId());
		</#if>
		<#if table.hasCreateDate>
		${classNameLower}.setCreateDate(new Date());
		</#if>
		<#if table.hasModifyBy>
		${classNameLower}.setModifyBy(userInfo.getUserId());
		</#if>
		<#if table.hasModifyDate>
		${classNameLower}.setModifyDate(new Date());
		</#if>
		${classNameLower}Repository.save(${classNameLower});
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject edit(Map<String, Object> params, UserInfo userInfo) {
        ${className} ${classNameLower} = (${className}) BeanUtils.mapToBean(params, ${className}.class);
		${className} ${classNameLower}InDb = ${classNameLower}Repository.findOne(${classNameLower}.get${table.pkColumn.columnName}());
		if(${classNameLower}InDb == null){
			return RestfulRetUtils.getErrorMsg("51006","当前编辑的对象不存在");
		}
		//合并两个javabean
		BeanUtils.mergeBean(${classNameLower}, ${classNameLower}InDb);
		<#if table.hasModifyBy>
		${classNameLower}InDb.setModifyBy(userInfo.getUserId());
		</#if>
		<#if table.hasModifyDate>
		${classNameLower}InDb.setModifyDate(new Date());
		</#if>
		${classNameLower}Repository.save(${classNameLower}InDb);
		return RestfulRetUtils.getRetSuccess();
	}

	<#if pageType = "1" >
	@Override
	public JSONObject del(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		<#if table.hasIsValid>
		//判断是否需要移除
		List<Long> idList = new ArrayList<Long>();
		for (int i = 0; i < ids.length; i++) {
			idList.add(StringUtils.toLong(ids[i]));
		}
		//批量更新（设置IsValid 为N）
		if(idList.size() > 0){
			${classNameLower}Repository.delEntity(Constants.IS_VALID_INVALID, idList);
		}
		<#else>
		for (int i = 0; i < ids.length; i++) {
			${classNameLower}Repository.delete(StringUtils.toLong(ids[i]));
		}
		</#if>
		return RestfulRetUtils.getRetSuccess();
	}
	<#elseif pageType = "2" >
	@Override
	public JSONObject del(String idsList, UserInfo userInfo) {
		Long id = StringUtils.toLong(idsList);
		List<Long> pids = new ArrayList<Long>();
		List<Long> allIds = new ArrayList<Long>();
		pids.add(id);
		allIds.add(id);
		int deep = Constants.DEFAULT_TREE_DEEP;
		while (!pids.isEmpty() && pids.size() > 0 && deep > 0) {
			List<${className}> list = ${classNameLower}Repository.getListByParentIds(pids);
			pids.clear();
			for (${className} obj : list) {
				pids.add(obj.get${table.pkColumn.columnName}());
				allIds.add(obj.get${table.pkColumn.columnName}());
			}
			deep--;
		}
		<#if table.hasIsValid>
		// 批量更新（设置IsValid 为N）
		if (allIds.size() > 0) {
			${classNameLower}Repository.delEntity(Constants.IS_VALID_INVALID, allIds);
		}
		<#else>
		for (int i = 0; i < allIds.size(); i++) {
			${classNameLower}Repository.delete(allIds.get(i));
		}
		</#if>
		return RestfulRetUtils.getRetSuccess();
	}
	</#if>

	<#-- 输出树列表的数据库操作 -->
	<#if pageType = "2" >
	public JSONObject getTreeList(){
		List<${className}> list = ${classNameLower}Repository.getTreeList();
		return getJsonTree(list);
	}

	public List<${className}> getListByParentIds(${className} ${classNameLower}){
		List<Long> idList = new ArrayList<>();
		Long parentId = (long) 0;
		if (${classNameLower} != null) {
			parentId = ${classNameLower}.getParentId();

		}
		List<${className}> treeList = new ArrayList<>();
		if (parentId == 0) {
			treeList = ${classNameLower}Repository.getListByRoot();
		} else {
			idList.add(parentId);
			treeList = ${classNameLower}Repository.getListByParentIds(idList);
		}
		return treeList;
	}
	
	/**
	 * 获取json格式的树
	 * @param list
	 * @return
	 */
	private JSONObject getJsonTree(List<${className}> list) {
		TreeNode root = new TreeNode("root", "all", null, false);
		Map<String, String> mapAttr = null;
		TreeNodeExtend treeNode = null;
		String id = "", text = "", parent = "";
		Tree tree = new Tree(true);
		// 添加一个自定义的根节点
		if (list == null || list.isEmpty()) {
			treeNode = new TreeNodeExtend("-1", "虚拟节点", "", false, null);
			tree.addNode(treeNode);
		}else{
			for (${className} obj : list) {
				if (obj == null || obj.get${table.pkColumn.columnName}() == null)
					continue;
				id = obj.get${table.pkColumn.columnName}().toString();
				text = obj.get${table.treeNodeName}();
				parent = obj.getParentId() == null ? "" : obj.getParentId().toString();

				mapAttr = new HashMap<String, String>();
				<#list table.columns as column>
				<#if column.simpleJavaType = "String">
				mapAttr.put("${column.columnNameFirstLower}", obj.get${column.columnName}());
				<#else>
				mapAttr.put("${column.columnNameFirstLower}", obj.get${column.columnName}() == null ? "" : obj.get${column.columnName}().toString());
				</#if>
				</#list>

				treeNode = new TreeNodeExtend(id, text, parent, false, mapAttr);
				tree.addNode(treeNode);
			}
		}
		String strTree = tree.getTreeJson(tree, root);
		return RestfulRetUtils.getRetSuccess(JSONArray.parseArray(strTree));
	}
	</#if>
}
