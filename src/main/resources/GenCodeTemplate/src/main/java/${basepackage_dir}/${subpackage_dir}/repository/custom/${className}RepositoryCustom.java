<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>
<#assign shortName = table.shortName>
package ${basepackage}.${subpackage}.repository.custom;

import java.util.List;
import java.util.Map;
import org.springframework.data.repository.NoRepositoryBean;
import ${basepackage}.${subpackage}.entity.${className};

/**
 * 自定义实体资源类接口
 * @version 1.0
 * @author
 *
 */
@NoRepositoryBean
public interface ${className}RepositoryCustom {

	/**
	 * 获取${className}列表
	 * @param ${classNameLower}
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getList(${className} ${classNameLower}, Map<String, Object> params, int pageIndex, int pageSize);
	
	/**
	 * 获取${className}列表总数
	 * @param ${classNameLower}
	 * @param params
	 * @return
	 */
	public int getListCount(${className} ${classNameLower}, Map<String, Object> params);
}