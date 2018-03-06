package com.gimplatform.core.service;

import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.TbGenerator;

public interface TbGeneratorService {

    /**
     * 获取列表
     * @param page
     * @param tbGenerator
     * @return
     */
    public JSONObject getList(Pageable page, TbGenerator tbGenerator);

    /**
     * 修复
     * @return
     */
    public JSONObject fixGenerator();

    /**
     * 获取最大ID值
     * @param primaryKey
     * @param table
     * @return
     */
    public String getMaxId(String primaryKey, String table);

}
