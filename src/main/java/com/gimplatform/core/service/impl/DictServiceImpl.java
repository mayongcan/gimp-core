package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.DictData;
import com.gimplatform.core.entity.DictType;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.query.Criteria;
import com.gimplatform.core.query.CriteriaFactory;
import com.gimplatform.core.repository.DictDataRepository;
import com.gimplatform.core.repository.DictTypeRepository;
import com.gimplatform.core.service.DictService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RedisUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 字典信息服务类
 * @author zzd
 *
 */
@Service
public class DictServiceImpl implements DictService{
	
    private static final Logger logger = LogManager.getLogger(DictServiceImpl.class);

	@Autowired
	private DictTypeRepository dictTypeRepository;
	
	@Autowired
	private DictDataRepository dictDataRepository;

	public boolean loadDictDataToCache() {
		List<Map<String, Object>> allDictList = dictDataRepository.getAllDictData();
		if(allDictList == null || allDictList.size() == 0) return false;
		else{
			RedisUtils.del(Constants.CACHE_REDIS_KEY_DICT);
			List<Map<String, Object>> tmpList = null;
			String dictType = "";
			for(Map<String, Object> map : allDictList){
				if(map == null || map.size() == 0) continue;
				dictType = MapUtils.getString(map, "DICTTYPE");
				if(StringUtils.isBlank(dictType)) continue;
				tmpList = new ArrayList<Map<String, Object>>();
				for(Map<String, Object> subMap : allDictList){
					if(subMap == null || subMap.size() == 0) continue;
					if(dictType.equals(MapUtils.getString(subMap, "DICTTYPE"))){
						tmpList.add(subMap);
					}
				}
				RedisUtils.hsetObject(Constants.CACHE_REDIS_KEY_DICT, dictType, tmpList, 0);
			}
			logger.info("将字典数据写入缓存");
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	public JSONArray getDictDataByDictTypeValue(String dictTypeValue, UserInfo userInfo) {
		JSONArray validArray = new JSONArray();
		if(StringUtils.isBlank(dictTypeValue)) return validArray;
		List<Map<String, Object>> allDictList = (List<Map<String, Object>>) RedisUtils.hgetObject(Constants.CACHE_REDIS_KEY_DICT, dictTypeValue);
		if(allDictList != null && allDictList.size() > 0){
			JSONObject validObj = null;
			Long tenantsId = -1L, organizerId = -1L;
			for(Map<String, Object> map : allDictList){
				if(map == null || map.size() == 0) continue;
				tenantsId = MapUtils.getLong(map, "TID");
				organizerId = MapUtils.getLong(map, "OID");
				if(tenantsId == null || organizerId == null) {
					logger.info("租户ID或组织ID为空[map=" + map.toString());
					continue;
				}
				validObj = new JSONObject();
				validObj.put("ID", map.get("ID"));
				validObj.put("NAME", map.get("NAME"));
				//判断字典是系统字典还是业务租户的字典
				if(tenantsId.equals(-1L)){
					validArray.add(validObj);
				}else{
					if(userInfo != null && userInfo.getTenantsId().equals(tenantsId) && (organizerId.equals(-1L) || userInfo.getOrganizerId().equals(organizerId))){
						validArray.add(validObj);
					}
				}
			}
		}else{
			logger.error("字典数据为空，请检查系统...");
		}
		return validArray;
	}

	@Override
	public String getDictDataValue(String dictDataValue, String dictTypeValue, UserInfo userInfo) {
		JSONArray dictArray = getDictDataByDictTypeValue(dictTypeValue, userInfo);
		if(dictArray == null || dictArray.isEmpty()) return "";
		JSONObject dictObj = null;
		String dictKey = "",dictValue = "";
		for(int i = 0;i < dictArray.size(); i++){
			dictObj = dictArray.getJSONObject(i);
			dictKey = dictObj.getString("ID");
			if(dictKey.equals(dictDataValue)){
				dictValue = dictObj.getString("NAME");
				break;
			}
		}
		return dictValue;
	}

	@Override
	public String getDictDataName(String dictDataName, String dictTypeValue, UserInfo userInfo) {
		JSONArray dictArray = getDictDataByDictTypeValue(dictTypeValue, userInfo);
		if(dictArray == null || dictArray.isEmpty()) return "";
		JSONObject dictObj = null;
		String dictName = "",dictValue = "";
		for(int i = 0;i < dictArray.size(); i++){
			dictObj = dictArray.getJSONObject(i);
			dictName = dictObj.getString("NAME");
			if(dictName.equals(dictDataName)){
				dictValue = dictObj.getString("ID");
				break;
			}
		}
		return dictValue;
	}

	@Override
	public Page<DictType> getDictTypeList(Pageable page, DictType dictType) {
		Criteria<DictType> criteria = new Criteria<DictType>();
		criteria.add(CriteriaFactory.like("name", dictType.getName()));
		criteria.add(CriteriaFactory.like("value", dictType.getValue()));
		criteria.add(CriteriaFactory.equal("shareType", dictType.getShareType()));
		criteria.add(CriteriaFactory.equal("isValid", "Y"));
		return dictTypeRepository.findAll(criteria, new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.ASC, "dictTypeId")));
	}

	@Override
	public JSONObject addDictType(DictType dictType, UserInfo userInfo) {
		dictType.setIsValid(Constants.IS_VALID_VALID);
		if(dictTypeRepository.findByValueAndIsValid(dictType.getValue(), Constants.IS_VALID_VALID) != null){
			return RestfulRetUtils.getErrorMsg("22006","字典类型值已存在");
		}
		dictType.setCreateDate(new Date());
		dictType.setCreateBy(userInfo.getUserId());
		dictType.setModifyBy(userInfo.getUserId());
		dictType.setModifyDate(new Date());
		dictTypeRepository.save(dictType);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject editDictType(DictType dictType, UserInfo userInfo) {
		dictType.setIsValid(Constants.IS_VALID_VALID);
		DictType dictTypeInDb = dictTypeRepository.findOne(dictType.getDictTypeId());
		if(dictTypeInDb == null){
			return RestfulRetUtils.getErrorMsg("22007","当前编辑的字典不存在");
		}
		if(dictTypeInDb != null && !dictTypeInDb.getValue().equals(dictType.getValue()) && 
				dictTypeRepository.findByValueAndIsValid(dictType.getValue(), Constants.IS_VALID_VALID) != null){
			return RestfulRetUtils.getErrorMsg("22006","字典类型值已存在");
		}
		//合并两个javabean
		BeanUtils.mergeBean(dictType, dictTypeInDb);

		dictTypeInDb.setModifyBy(userInfo.getUserId());
		dictTypeInDb.setModifyDate(new Date());
		dictTypeRepository.save(dictTypeInDb);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject delDictType(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		List<Long> idList = new ArrayList<Long>();
		//判断是否需要移除
		for (int i = 0; i < ids.length; i++) {
			DictType dictType = dictTypeRepository.getOne(StringUtils.toLong(ids[i]));
			if(dictType != null && Constants.IS_VALID_VALID.equals(dictType.getIsValid())){
				idList.add(StringUtils.toLong(ids[i]));
			}
		}
		//批量更新（设置IsValid 为N）
		if(idList.size() > 0)
			dictTypeRepository.delDictType(Constants.IS_VALID_INVALID, userInfo.getUserId(), new Date(), idList);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public Page<DictData> getDictDataList(Pageable page, DictData dictData) {
		Criteria<DictData> criteria = new Criteria<DictData>();
		criteria.add(CriteriaFactory.equal("isValid", "Y"));
		criteria.add(CriteriaFactory.equal("dictTypeId", dictData.getDictTypeId()));
		criteria.add(CriteriaFactory.or(CriteriaFactory.equal("tenantsId", -1), CriteriaFactory.equal("tenantsId", dictData.getTenantsId())));
		criteria.add(CriteriaFactory.or(CriteriaFactory.equal("organizerId", dictData.getOrganizerId()), CriteriaFactory.equal("organizerId", -1)));
		criteria.add(CriteriaFactory.like("name", dictData.getName()));
		criteria.add(CriteriaFactory.like("value", dictData.getValue()));
		return dictDataRepository.findAll(criteria, new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.ASC, "dispOrder")));
	}

	@Override
	public JSONObject addDictDataType(DictData dictData, UserInfo userInfo, String dataShare) {
		dictData.setIsValid(Constants.IS_VALID_VALID);
		if("-1".equals(dataShare)){
			dictData.setTenantsId(-1L);
			dictData.setOrganizerId(-1L);
		}else{
			dictData.setTenantsId(userInfo.getTenantsId());
			if("-2".equals(dataShare)){
				dictData.setOrganizerId(-1L);
			}else{
				dictData.setOrganizerId(userInfo.getTenantsId());
			}
		}
		if(dictDataRepository.getDictDataByParams( Constants.IS_VALID_VALID, dictData.getValue(), dictData.getDictTypeId(), userInfo.getTenantsId()) != null){
			return RestfulRetUtils.getErrorMsg("22012","字典数据值已存在");
		}

		dictData.setCreateDate(new Date());
		dictData.setCreateBy(userInfo.getUserId());
		dictData.setModifyBy(userInfo.getUserId());
		dictData.setModifyDate(new Date());
		dictDataRepository.save(dictData);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject editDictDataType(DictData dictData, UserInfo userInfo, String dataShare) {
		dictData.setIsValid(Constants.IS_VALID_VALID);
		if("-1".equals(dataShare)){
			dictData.setTenantsId(-1L);
			dictData.setOrganizerId(-1L);
		}else{
			dictData.setTenantsId(userInfo.getTenantsId());
			if("-2".equals(dataShare)){
				dictData.setOrganizerId(-1L);
			}else{
				dictData.setOrganizerId(userInfo.getTenantsId());
			}
		}
		DictData dictDataInDb = dictDataRepository.findOne(dictData.getDictDataId());
		if(dictDataInDb == null){
			return RestfulRetUtils.getErrorMsg("22013","当前编辑的字典数据不存在");
		}
		if(dictDataInDb != null && !dictDataInDb.getValue().equals(dictData.getValue()) && 
				dictDataRepository.getDictDataByParams( Constants.IS_VALID_VALID, dictData.getValue(), dictData.getDictTypeId(), userInfo.getTenantsId()) != null){
			return RestfulRetUtils.getErrorMsg("22012","字典数据值已存在");
		}
		//合并两个javabean
		BeanUtils.mergeBean(dictData, dictDataInDb);

		dictDataInDb.setModifyBy(userInfo.getUserId());
		dictDataInDb.setModifyDate(new Date());
		dictDataRepository.save(dictDataInDb);
		return RestfulRetUtils.getRetSuccess();
	}


	@Override
	public JSONObject delDictDataType(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		List<Long> idList = new ArrayList<Long>();
		//判断是否需要移除
		for (int i = 0; i < ids.length; i++) {
			DictData dictData = dictDataRepository.getOne(StringUtils.toLong(ids[i]));
			if(dictData != null && Constants.IS_VALID_VALID.equals(dictData.getIsValid())){
				idList.add(StringUtils.toLong(ids[i]));
			}
		}
		//批量更新（设置IsValid 为N）
		if(idList.size() > 0)
			dictDataRepository.delDictData(Constants.IS_VALID_INVALID, userInfo.getUserId(), new Date(), idList);
		return RestfulRetUtils.getRetSuccess();
	}
}
