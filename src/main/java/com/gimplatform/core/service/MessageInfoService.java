/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.service;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.UserInfo;

import com.gimplatform.core.entity.MessageInfo;
import com.gimplatform.core.entity.MessageUser;

/**
 * 服务类接口
 * @version 1.0
 * @author
 */
public interface MessageInfoService {

    /**
     * 获取列表
     * @param page
     * @param messageInfo
     * @return
     */
    public JSONObject getList(Pageable page, MessageInfo messageInfo, Map<String, Object> params);

    /**
     * 新增
     * @param messageInfo
     * @param userInfo
     * @return
     */
    public JSONObject add(MessageInfo messageInfo, UserInfo userInfo, String userIdList, boolean sendAll, boolean isNotice);

    /**
     * 编辑
     * @param messageInfo
     * @param userInfo
     * @return
     */
    public JSONObject edit(MessageInfo messageInfo, UserInfo userInfo, String userIdList, boolean sendAll);

    /**
     * 删除
     * @param idsList
     * @param userInfo
     * @return
     */
    public JSONObject del(String idsList, UserInfo userInfo);

    /**
     * 新增
     * @param messageInfo
     * @param userInfo
     * @return
     */
    public JSONObject addAndSend(MessageInfo messageInfo, UserInfo userInfo, String userIdList, boolean sendAll, boolean isNotice);

    /**
     * 发送消息
     * @param idsList
     * @param userInfo
     * @return
     */
    public JSONObject send(String idsList, UserInfo userInfo);

    /**
     * 获取我的消息
     * @param page
     * @param messageUser
     * @param params
     * @return
     */
    public JSONObject getMyMessage(Pageable page, MessageUser messageUser, Map<String, Object> params);

    /**
     * 设置消息已读
     * @param userMessageId
     * @return
     */
    public JSONObject setMessageRead(Long userMessageId);

    /**
     * 获取未读消息数
     * @param userInfo
     * @return
     */
    public int getUnReadMessageCount(UserInfo userInfo);

}
