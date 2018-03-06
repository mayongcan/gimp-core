package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.ClientVersion;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.query.Criteria;
import com.gimplatform.core.query.CriteriaFactory;
import com.gimplatform.core.repository.ClientVersionRepository;
import com.gimplatform.core.service.ClientVersionService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

@Service
public class ClientVersionServiceImpl implements ClientVersionService {

    @Autowired
    private ClientVersionRepository clientVersionRepository;

    @Override
    public Page<ClientVersion> getList(Pageable page, ClientVersion clientVersion) {
        Criteria<ClientVersion> criteria = new Criteria<ClientVersion>();
        criteria.add(CriteriaFactory.like("name", clientVersion.getName()));
        criteria.add(CriteriaFactory.equal("isValid", "Y"));
        return clientVersionRepository.findAll(criteria, new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.DESC, "clientId")));
    }

    @Override
    public JSONObject add(ClientVersion clientVersion, UserInfo userInfo) {
        clientVersion.setIsValid(Constants.IS_VALID_VALID);
        clientVersion.setCreateBy(userInfo.getUserId());
        clientVersion.setCreateDate(new Date());
        clientVersionRepository.save(clientVersion);
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public JSONObject edit(ClientVersion clientVersion, UserInfo userInfo) {
        ClientVersion cdmsVsmClientInDb = clientVersionRepository.findOne(clientVersion.getClientId());
        if (cdmsVsmClientInDb == null) {
            return RestfulRetUtils.getErrorMsg("51006", "当前编辑的规则不存在");
        }
        // 合并两个javabean
        BeanUtils.mergeBean(clientVersion, cdmsVsmClientInDb);
        clientVersionRepository.save(cdmsVsmClientInDb);
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public JSONObject del(String idsList, UserInfo userInfo) {
        String[] ids = idsList.split(",");
        List<Long> idList = new ArrayList<Long>();
        // 判断是否需要移除
        for (int i = 0; i < ids.length; i++) {
            idList.add(StringUtils.toLong(ids[i]));
            clientVersionRepository.delete(StringUtils.toLong(ids[i]));
        }
        return RestfulRetUtils.getRetSuccess();
    }

}
