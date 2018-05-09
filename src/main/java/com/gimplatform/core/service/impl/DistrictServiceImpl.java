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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.District;
import com.gimplatform.core.repository.DistrictRepository;
import com.gimplatform.core.service.DistrictService;
import com.gimplatform.core.utils.RedisUtils;
import com.gimplatform.core.utils.StringUtils;

@Service
public class DistrictServiceImpl implements DistrictService {

    private static final Logger logger = LogManager.getLogger(DistrictServiceImpl.class);

    @Autowired
    private DistrictRepository districtRepository;

    @Override
    public boolean loadDistrictDataToCache() {
        List<District> list = districtRepository.findAll();
        if (list == null || list.size() == 0)
            return false;
        else {
            RedisUtils.del(Constants.CACHE_REDIS_KEY_DISTRICT);
            List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
            Map<String, Object> tmpMap = null;
            for (District obj : list) {
                // 先取出最顶层
                if (obj.getParentId().equals(0L)) {
                    tmpMap = new HashMap<String, Object>();
                    tmpMap.put("ID", obj.getId());
                    tmpMap.put("NAME", obj.getName());
                    tmpList.add(tmpMap);
                }
            }
            // 将第一层写入redis
            RedisUtils.hsetObject(Constants.CACHE_REDIS_KEY_DISTRICT, "PARENT:0", tmpList, 0);
            // 递归返回下层信息
            loadSubDistrictDataToCache(list, tmpList);
            logger.info("将区域信息写入缓存");
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDistrictListByParentId(Long parentId) {
        return (List<Map<String, Object>>) RedisUtils.hgetObject(Constants.CACHE_REDIS_KEY_DISTRICT, "PARENT:" + parentId);
    }

    @Override
    public String getAreaCode(String areaName) {
        List<String> areaNameList = StringUtils.splitToList(areaName, "-", 3);
        String areaCode = "";
        List<Map<String, Object>> provinceList = getDistrictListByParentId(0L);
        boolean isEnd = false;
        for(int i = 0; i < provinceList.size(); i++) { 
            if(areaNameList.get(0).equals(MapUtils.getString(provinceList.get(i), "NAME"))) {
                areaCode += MapUtils.getLong(provinceList.get(i), "ID") + ",";
                List<Map<String, Object>> cityList = getDistrictListByParentId(MapUtils.getLong(provinceList.get(i), "ID"));
                if(cityList != null) {
                    for(int j = 0; j < cityList.size(); j++) {
                        if(areaNameList.get(1).equals(MapUtils.getString(cityList.get(j), "NAME"))) {
                            areaCode += MapUtils.getLong(cityList.get(j), "ID") + ",";
                            List<Map<String, Object>> areaList = getDistrictListByParentId(MapUtils.getLong(cityList.get(j), "ID"));
                            if(areaList != null) {
                                for(int k = 0; k < areaList.size(); k++) {
                                    if(areaNameList.get(2).equals(MapUtils.getString(areaList.get(k), "NAME"))) {
                                        areaCode += MapUtils.getLong(areaList.get(k), "ID") + ",";
                                        isEnd = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if(isEnd) break;
                    }
                }
            }
            if(isEnd) break;
        }
        if(!StringUtils.isBlank(areaCode)) areaCode = areaCode.substring(0, areaCode.length() - 1);
        return areaCode;
    }

    @Override
    public JSONArray getAppDistrictJsonArray() {
        List<Map<String, Object>> provinceList = getDistrictListByParentId(0L);
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < provinceList.size(); i++) { 
            List<Map<String, Object>> cityList = getDistrictListByParentId(MapUtils.getLong(provinceList.get(i), "ID"));
            JSONObject provinceJson = new JSONObject();
            JSONArray cityArray = new JSONArray();
            if(cityList != null) {
                for(int j = 0; j < cityList.size(); j++) {
                    List<Map<String, Object>> areaList = getDistrictListByParentId(MapUtils.getLong(cityList.get(j), "ID"));
                    List<String> areaNameList = new ArrayList<String>();
                    JSONObject cityJson = new JSONObject();
                    if(areaList != null) {
                        for(int k = 0; k < areaList.size(); k++) {
                            areaNameList.add(MapUtils.getString(areaList.get(k), "NAME"));
                        }
                    }else {
                        //设置一个空值
                        areaNameList.add("");
                    }
                    cityJson.put("name", MapUtils.getString(cityList.get(j), "NAME"));
                    cityJson.put("area", areaNameList);
                    cityArray.add(cityJson);
                }
            }else {
                //设置一个空值
                JSONObject cityJson = new JSONObject();
                List<String> areaNameList = new ArrayList<String>();
                areaNameList.add("");
                cityJson.put("name", "");
                cityJson.put("area", areaNameList);
                cityArray.add(cityJson);
            }
            provinceJson.put("name", MapUtils.getString(provinceList.get(i), "NAME"));
            provinceJson.put("city", cityArray);
            jsonArray.add(provinceJson);
        }
        return jsonArray;
    }

    /**
     * 递归写入缓存
     * @param list
     * @param subList
     */
    private void loadSubDistrictDataToCache(List<District> list, List<Map<String, Object>> subList) {
        List<Map<String, Object>> tmpList = null;
        Map<String, Object> tmpMap = null;
        for (Map<String, Object> map : subList) {
            Long id = MapUtils.getLong(map, "ID");
            if (id == null)
                continue;
            tmpList = new ArrayList<Map<String, Object>>();
            for (District obj : list) {
                if (obj.getParentId().equals(id)) {
                    tmpMap = new HashMap<String, Object>();
                    tmpMap.put("ID", obj.getId());
                    tmpMap.put("NAME", obj.getName());
                    tmpList.add(tmpMap);
                }
            }
            if (tmpList != null && tmpList.size() > 0) {
                // 写入redis
                RedisUtils.hsetObject(Constants.CACHE_REDIS_KEY_DISTRICT, "PARENT:" + id, tmpList, 0);
                // 递归下层
                loadSubDistrictDataToCache(list, tmpList);
            }
        }
    }

}
