package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface FuncInfoRepositoryCustom {

	/**
	 * 获取所有权限数据
	 * @return
	 */
    public List<Map<String, Object>> getAllFuncData();  
    
    /**
     * 获取权限树列表
     * @param idList
     * @param isShow
     * @param folderFlag
     * @return
     */
    public List<Map<String, Object>> getFuncTreeList(List<Long> idList, String isShow, String folderFlag);
    
    /**
     * 根据租户ID获取权限列表
     * @param tenantsId
     * @return
     */
	public List<Map<String, Object>> getFuncByTenantsId(Long tenantsId);
}
