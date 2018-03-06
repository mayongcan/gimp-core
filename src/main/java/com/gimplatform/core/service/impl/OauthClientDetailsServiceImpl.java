package com.gimplatform.core.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.OauthClientDetails;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.query.Criteria;
import com.gimplatform.core.query.CriteriaFactory;
import com.gimplatform.core.repository.OauthClientDetailsRepository;
import com.gimplatform.core.service.OauthClientDetailsService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

@Service
public class OauthClientDetailsServiceImpl implements OauthClientDetailsService {

    protected static final Logger logger = LogManager.getLogger(OauthClientDetailsServiceImpl.class);

    @Autowired
    private OauthClientDetailsRepository oauthClientDetailsRepository;

    @Override
    public Page<OauthClientDetails> getOauthClientList(Pageable page, OauthClientDetails oauthClientDetails) {
        Criteria<OauthClientDetails> criteria = new Criteria<OauthClientDetails>();
        criteria.add(CriteriaFactory.like("clientId", oauthClientDetails.getClientId()));
        return oauthClientDetailsRepository.findAll(criteria, new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.ASC, "clientId")));
    }

    @Override
    public JSONObject addOauthClient(OauthClientDetails oauthClientDetails, UserInfo userInfo) {
        if (oauthClientDetailsRepository.findByClientId(oauthClientDetails.getClientId()).size() > 0) {
            return RestfulRetUtils.getErrorMsg("26005", "当前授权客户端名称已存在");
        }
        oauthClientDetailsRepository.save(oauthClientDetails);
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public JSONObject editOauthClient(OauthClientDetails oauthClientDetails, UserInfo userInfo, String oldClientId) {
        OauthClientDetails oauthClientDetailsInDb = null;
        if (StringUtils.isBlank(oldClientId))
            oauthClientDetailsInDb = oauthClientDetailsRepository.findOne(oauthClientDetails.getClientId());
        else
            oauthClientDetailsInDb = oauthClientDetailsRepository.findOne(oldClientId);
        if (oauthClientDetailsInDb == null) {
            return RestfulRetUtils.getErrorMsg("26006", "当前编辑的授权客户端名称不存在");
        }
        if (oauthClientDetailsInDb != null && !oauthClientDetailsInDb.getClientId().equals(oauthClientDetails.getClientId()) && oauthClientDetailsRepository.findByClientId(oauthClientDetails.getClientId()).size() > 0) {
            return RestfulRetUtils.getErrorMsg("26005", "当前授权客户端名称已存在");
        }
        // 删除旧记录
        if (!StringUtils.isBlank(oldClientId)) {
            oauthClientDetailsRepository.delete(oldClientId);
            oauthClientDetailsRepository.flush();
        }
        // 合并两个javabean
        BeanUtils.mergeBean(oauthClientDetails, oauthClientDetailsInDb);
        oauthClientDetailsRepository.save(oauthClientDetailsInDb);
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public JSONObject delOauthClient(String idsList, UserInfo userInfo) {
        String[] ids = idsList.split(",");
        for (int i = 0; i < ids.length; i++) {
            if (!StringUtils.isBlank(ids[i]))
                oauthClientDetailsRepository.delete(ids[i]);
        }
        return RestfulRetUtils.getRetSuccess();
    }

}
