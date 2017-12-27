<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>
<#assign shortName = table.shortName>
package ${basepackage}.${subpackage}.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
<#if table.hasIsValid>
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
</#if>

import ${basepackage}.${subpackage}.entity.${className};
import ${basepackage}.${subpackage}.repository.custom.${className}RepositoryCustom;

/**
 * 实体资源类
 * @version 1.0
 * @author
 *
 */
@Repository
public interface ${className}Repository extends JpaRepository<${className}, Long>, JpaSpecificationExecutor<${className}>, ${className}RepositoryCustom {
	
	<#-- 输出树列表的数据库操作 -->
	<#if pageType = "2" && table.hasParentId >
	/**
	 * 获取树列表
	 * @return
	 */
	<#if table.hasIsValid>
	@Query(value = "SELECT tb.* "
			+ "FROM ${table.sqlName} tb "
			+ "WHERE tb.IS_VALID = 'Y' "
			+ "ORDER BY PARENT_ID, ${table.treeNodeOrder}, ${table.pkColumn.sqlName}", nativeQuery = true)
	<#else>
	@Query(value = "SELECT tb.* "
			+ "FROM ${table.sqlName} tb "
			+ "ORDER BY PARENT_ID, ${table.treeNodeOrder}, ${table.pkColumn.sqlName}", nativeQuery = true)
	</#if>
    public List<${className}> getTreeList();  

	/**
	 * 根据父ID列表获取ID列表
	 * @param idList
	 * @return
	 */
	<#if table.hasIsValid>
	@Query(value = "SELECT * FROM ${table.sqlName} WHERE IS_VALID='Y' AND PARENT_ID IN (:idList)", nativeQuery = true)
	<#else>
	@Query(value = "SELECT * FROM ${table.sqlName} WHERE PARENT_ID IN (:idList)", nativeQuery = true)
	</#if>
    public List<${className}> getListByParentIds(@Param("idList")List<Long> idList); 
	
	/**
	 * 根据父ID列表获取ID列表
	 * @param idList
	 * @return
	 */
	<#if table.hasIsValid>
	@Query(value = "SELECT * FROM ${table.sqlName} WHERE IS_VALID='Y' AND PARENT_ID is null", nativeQuery = true)
	<#else>
	@Query(value = "SELECT * FROM ${table.sqlName} WHERE PARENT_ID is null", nativeQuery = true)
	</#if>
    public List<${className}> getListByRoot();  
    </#if>
	
    <#if table.hasIsValid>
	/**
	 * 删除信息（将信息的IS_VALID设置为N）
	 * @param isValid
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE ${table.sqlName} "
			+ "SET IS_VALID = :isValid "
			+ "WHERE ${table.pkColumn.sqlName} IN (:idList)", nativeQuery = true)
	public void delEntity(@Param("isValid")String isValid, @Param("idList")List<Long> idList);
	</#if>
	
}