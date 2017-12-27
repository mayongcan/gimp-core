package com.gimplatform.core.service;

import java.util.List;
import java.util.Map;

/**
 * 区域服务类接口
 * @author zzd
 *
 */
public interface DistrictService {

	/**
	 * 加载字典数据到缓存
	 * @return
	 */
	public boolean loadDistrictDataToCache();

	/**
	 * 通过父ID获取列表
	 * @param parentId
	 * @return
	 */
	public List<Map<String, Object>> getDistrictListByParentId(Long parentId);
}
