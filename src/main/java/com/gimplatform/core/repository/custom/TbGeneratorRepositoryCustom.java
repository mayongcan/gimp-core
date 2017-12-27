package com.gimplatform.core.repository.custom;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TbGeneratorRepositoryCustom {

	/**
	 * 获取组大值
	 * @param primaryKey
	 * @param table
	 * @return
	 */
    public String getMaxId(String primaryKey, String table); 
}
