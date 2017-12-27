package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * 扩展接口
 * @author zzd
 *
 */
@NoRepositoryBean
public interface DictDataRepositoryCustom {

	/**
	 * 获取所有字典数据
	 * @return
	 */
    public List<Map<String, Object>> getAllDictData();  
}
