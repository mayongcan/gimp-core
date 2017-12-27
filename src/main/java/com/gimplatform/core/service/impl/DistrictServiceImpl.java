package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.District;
import com.gimplatform.core.repository.DistrictRepository;
import com.gimplatform.core.service.DistrictService;
import com.gimplatform.core.utils.RedisUtils;

@Service
public class DistrictServiceImpl implements DistrictService{
	
    private static final Logger logger = LogManager.getLogger(DistrictServiceImpl.class);
	
	@Autowired
	private DistrictRepository districtRepository;

	@Override
	public boolean loadDistrictDataToCache() {
		List<District> list = districtRepository.findAll();
		if(list == null || list.size() == 0) return false;
		else{
			RedisUtils.del(Constants.CACHE_REDIS_KEY_DISTRICT);
			List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
			Map<String, Object> tmpMap = null;
			for(District obj : list){
				//先取出最顶层
				if(obj.getParentId().equals(0L)){
					tmpMap = new HashMap<String, Object>();
					tmpMap.put("ID", obj.getId());
					tmpMap.put("NAME", obj.getName());
					tmpList.add(tmpMap);
				}
			}
			//将第一层写入redis
			RedisUtils.hsetObject(Constants.CACHE_REDIS_KEY_DISTRICT, "PARENT:0", tmpList, 0);
			//递归返回下层信息
			loadSubDistrictDataToCache(list, tmpList);
			logger.info("将区域信息写入缓存");
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDistrictListByParentId(Long parentId) {
		return (List<Map<String, Object>>) RedisUtils.hgetObject(Constants.CACHE_REDIS_KEY_DISTRICT, "PARENT:" + parentId);
	}
	
	/**
	 * 递归写入缓存
	 * @param list
	 * @param subList
	 */
	private void loadSubDistrictDataToCache(List<District> list, List<Map<String, Object>> subList){
		List<Map<String, Object>> tmpList = null;
		Map<String, Object> tmpMap = null;
		for(Map<String, Object> map : subList){
			Long id = MapUtils.getLong(map, "ID");
			if(id == null) continue;
			tmpList = new ArrayList<Map<String, Object>>();
			for(District obj : list){
				if(obj.getParentId().equals(id)){
					tmpMap = new HashMap<String, Object>();
					tmpMap.put("ID", obj.getId());
					tmpMap.put("NAME", obj.getName());
					tmpList.add(tmpMap);
				}
			}
			if(tmpList != null && tmpList.size() > 0){
				//写入redis
				RedisUtils.hsetObject(Constants.CACHE_REDIS_KEY_DISTRICT, "PARENT:" + id, tmpList, 0);
				//递归下层
				loadSubDistrictDataToCache(list, tmpList);
			}
		}
	}

}
