<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>
<#assign shortName = table.shortName>
package ${basepackage}.${subpackage}.repository.impl;

import java.util.List;
import java.util.Map;
import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.utils.StringUtils;

import ${basepackage}.${subpackage}.entity.${className};
import ${basepackage}.${subpackage}.repository.custom.${className}RepositoryCustom;

public class ${className}RepositoryImpl extends BaseRepository implements ${className}RepositoryCustom{

	private static final String SQL_GET_LIST = "SELECT <#list table.columns as column>tb.${column.sqlName} as \"${column.columnNameFirstLower}\"<#if column_has_next>, </#if></#list> "
			+ "FROM ${table.sqlName} tb "
			+ "WHERE 1 = 1 <#if table.hasIsValid>AND tb.IS_VALID = 'Y'</#if>";

	private static final String SQL_GET_LIST_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM ${table.sqlName} tb "
			+ "WHERE 1 = 1 <#if table.hasIsValid>AND tb.IS_VALID = 'Y'</#if>";
	
	public List<Map<String, Object>> getList(${className} ${classNameLower}, Map<String, Object> params, int pageIndex, int pageSize) {
		//生成查询条件
		SqlParams sqlParams = genListWhere(SQL_GET_LIST, ${classNameLower}, params);
		//添加分页和排序
		sqlParams = getPageableSql(sqlParams, pageIndex, pageSize, " tb.${table.pkColumn.sqlName} DESC ", " \"${table.pkColumn.columnNameFirstLower}\" DESC ");
		return getResultList(sqlParams);
	}

	public int getListCount(${className} ${classNameLower}, Map<String, Object> params) {
		//生成查询条件
		SqlParams sqlParams = genListWhere(SQL_GET_LIST_COUNT, ${classNameLower}, params);
		return getResultListTotalCount(sqlParams);
	}

	/**
	 * 生成查询条件
	 * @param sql
	 * @param params
	 * @return
	 */
	private SqlParams genListWhere(String sql, ${className} ${classNameLower}, Map<String, Object> params){
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(sql);
		<#list table.columns as column>
		<#-- 判断是否需要搜索 -->
		<#if column.search>
		<#if column.simpleJavaType = "String">
		if (${classNameLower} != null && !StringUtils.isBlank(${classNameLower}.get${column.columnName}())) {
		<#elseif column.simpleJavaType = "Date">
		if (!StringUtils.isBlank(MapUtils.getString(params, "${column.columnNameFirstLower}Begin")) && !StringUtils.isBlank(MapUtils.getString(params, "${column.columnNameFirstLower}End"))) {
		<#else>
		if (${classNameLower} != null && ${classNameLower}.get${column.columnName}() != null) {
		</#if>
			<#if column.searchType = "1">
			sqlParams.querySql.append(" AND tb.${column.sqlName} = :${column.columnNameFirstLower} ");
			<#elseif column.searchType = "2">
            sqlParams.querySql.append(getLikeSql("tb.${column.sqlName}", ":${column.columnNameFirstLower}"));
			<#elseif column.searchType = "3">
			sqlParams.querySql.append(" AND tb.${column.sqlName} between :${column.columnNameFirstLower}Begin AND :${column.columnNameFirstLower}End ");
			<#else>
			sqlParams.querySql.append(" AND tb.${column.sqlName} = :${column.columnNameFirstLower} ");
			</#if>
			<#-- 日期查询需要填入两个参数 -->
			<#if column.searchType = "3">
			sqlParams.paramsList.add("${column.columnNameFirstLower}Begin");
			sqlParams.paramsList.add("${column.columnNameFirstLower}End");
			sqlParams.valueList.add(MapUtils.getString(params, "${column.columnNameFirstLower}Begin"));
			sqlParams.valueList.add(MapUtils.getString(params, "${column.columnNameFirstLower}End"));
			<#else>
			sqlParams.paramsList.add("${column.columnNameFirstLower}");
			sqlParams.valueList.add(${classNameLower}.get${column.columnName}());
			</#if>
		}
		</#if>
		</#list>
        return sqlParams;
	}
}