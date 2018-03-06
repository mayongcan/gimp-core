/*
 * Copyright(c) 2018 gimplatform All rights reserved.
 * distributed with this file and available online at
 */
package com.gimplatform.core.service;

import org.springframework.data.domain.Pageable;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.UserInfo;

import com.gimplatform.core.entity.GenCode;

/**
 * 服务类接口
 * @version 1.0
 * @author
 */
public interface GenCodeService {

    /**
     * 获取列表
     * @param page
     * @param genCode
     * @return
     */
    public JSONObject getList(Pageable page, GenCode genCode);

    /**
     * 新增
     * @param genCode
     * @param userInfo
     * @return
     */
    public JSONObject add(GenCode genCode, UserInfo userInfo);

    /**
     * 编辑
     * @param genCode
     * @param userInfo
     * @return
     */
    public JSONObject edit(GenCode genCode, UserInfo userInfo);

    /**
     * 删除
     * @param idsList
     * @param userInfo
     * @return
     */
    public JSONObject del(String idsList, UserInfo userInfo);

    /**
     * 获取对象
     * @param codeId
     * @return
     */
    public GenCode getGenCode(Long codeId);

}
