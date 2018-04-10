package com.gimplatform.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.DictData;
import com.gimplatform.core.entity.DictType;
import com.gimplatform.core.entity.UserInfo;

/**
 * 字典服务类接口
 * @author zzd
 */
public interface DictService {

    /**
     * 加载字典数据到缓存
     * @return
     */
    public boolean loadDictDataToCache();

    /**
     * 根据关键字获取字典数据（从缓存中获取）
     * @param dictTypeValue
     * @param userInfo
     * @return
     */
    public JSONArray getDictDataByDictTypeValue(String dictTypeValue, UserInfo userInfo);

    /**
     * 获取字典值
     * @param dictDataValue
     * @param dictTypeValue
     * @param userInfo
     * @return
     */
    public String getDictDataValue(String dictDataValue, String dictTypeValue, UserInfo userInfo);

    /**
     * 根据字典名称获取字典值
     * @param dictDataKey
     * @param dictTypeValue
     * @param userInfo
     * @return
     */
    public String getDictDataName(String dictDataName, String dictTypeValue, UserInfo userInfo);

    /**
     * 获取字典类型列表
     * @param page
     * @param dictType
     * @return
     */
    public Page<DictType> getDictTypeList(Pageable page, DictType dictType);

    /**
     * 新增字典
     * @param dictType
     * @param userInfo
     * @return
     */
    public JSONObject addDictType(DictType dictType, UserInfo userInfo);

    /**
     * 编辑字典
     * @param dictType
     * @param userInfo
     * @return
     */
    public JSONObject editDictType(DictType dictType, UserInfo userInfo);

    /**
     * 删除字典
     * @param idsList
     * @param userInfo
     * @return
     */
    public JSONObject delDictType(String idsList, UserInfo userInfo);

    /**
     * 获取字典数据列表
     * @param page
     * @param dictData
     * @return
     */
    public Page<DictData> getDictDataList(Pageable page, DictData dictData);

    /**
     * 新增字典数据
     * @param dictType
     * @param userInfo
     * @return
     */
    public JSONObject addDictDataType(DictData dictData, UserInfo userInfo, String dataShare);

    /**
     * 编辑字典数据
     * @param dictType
     * @param userInfo
     * @return
     */
    public JSONObject editDictDataType(DictData dictData, UserInfo userInfo, String dataShare);

    /**
     * 删除字典数据
     * @param idsList
     * @param userInfo
     * @return
     */
    public JSONObject delDictDataType(String idsList, UserInfo userInfo);
    
    /**
     * 更新字典数据排序
     * @param params
     * @return
     */
    public JSONObject updateDictDataSort(String params);
}
