package com.gimplatform.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.OauthClientDetails;
import com.gimplatform.core.entity.UserInfo;

public interface OauthClientDetailsService {

    /**
     * 获取授权列表
     * @param oauthClientDetails
     * @return
     */
    public Page<OauthClientDetails> getOauthClientList(Pageable page, OauthClientDetails oauthClientDetails);

    /**
     * 新增授权
     * @param oauthClientDetails
     * @param userInfo
     * @return
     */
    public JSONObject addOauthClient(OauthClientDetails oauthClientDetails, UserInfo userInfo);

    /**
     * 编辑授权
     * @param oauthClientDetails
     * @param userInfo
     * @param oldClientId
     * @return
     */
    public JSONObject editOauthClient(OauthClientDetails oauthClientDetails, UserInfo userInfo, String oldClientId);

    /**
     * 删除授权
     * @param idsList
     * @param userInfo
     * @return
     */
    public JSONObject delOauthClient(String idsList, UserInfo userInfo);

}
