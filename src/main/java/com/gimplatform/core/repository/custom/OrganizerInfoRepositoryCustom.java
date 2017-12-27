package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OrganizerInfoRepositoryCustom {

	/**
	 * 获取组织树
	 * @param parentOrgId
	 * @param organizerId
	 * @param filterDept
	 * @param filterPost
	 * @param tenantsId
	 * @return
	 */
	public List<Map<String, Object>> getOrganizerTree(Map<String, Object> params);
	
}
