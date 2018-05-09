package com.gimplatform.core.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

/**
 * 区域服务类接口
 * @author zzd
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
    
    /**
     * 根据区域名称获取区域代码
     * @param areaName
     * @return
     */
    public String getAreaCode(String areaName);
    
    /**
     * 生成用于APP的区域列表
     * @return
     */
    public JSONArray getAppDistrictJsonArray();
}
