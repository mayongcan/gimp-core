/*
 * Copyright(c) 2018 gimplatform All rights reserved.
 * distributed with this file and available online at
 */
package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.query.Criteria;
import com.gimplatform.core.query.CriteriaFactory;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;
import com.gimplatform.core.service.GenCodeService;
import com.gimplatform.core.entity.GenCode;
import com.gimplatform.core.repository.GenCodeRepository;

@Service
public class GenCodeServiceImpl implements GenCodeService {

    @Autowired
    private GenCodeRepository genCodeRepository;

    @Override
    public JSONObject getList(Pageable page, GenCode genCode) {
        Criteria<GenCode> criteria = new Criteria<GenCode>();
        criteria.add(CriteriaFactory.like("moduleName", genCode.getModuleName()));
        criteria.add(CriteriaFactory.like("basePackage", genCode.getBasePackage()));
        criteria.add(CriteriaFactory.like("subPackage", genCode.getSubPackage()));
        criteria.add(CriteriaFactory.like("tableName", genCode.getTableName()));
        criteria.add(CriteriaFactory.like("tableDesc", genCode.getTableDesc()));
        criteria.add(CriteriaFactory.equal("pageType", genCode.getPageType()));
        criteria.add(CriteriaFactory.equal("isValid", Constants.IS_VALID_VALID));
        Page<GenCode> pageList = genCodeRepository.findAll(criteria, new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.ASC, "codeId")));
        return RestfulRetUtils.getRetSuccessWithPage(pageList.getContent(), pageList.getTotalElements());
    }

    @Override
    public JSONObject add(GenCode genCode, UserInfo userInfo) {
        // 判断表名是否已存在
        List<GenCode> list = genCodeRepository.findByTableName(genCode.getTableName());
        if (list != null && list.size() > 0) {
            return RestfulRetUtils.getErrorMsg("51006", "当前输入的表名已存在，请重新填写");
        }
        genCode.setIsValid(Constants.IS_VALID_VALID);
        genCode.setCreateBy(userInfo.getUserId());
        genCode.setCreateDate(new Date());
        genCodeRepository.save(genCode);
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public JSONObject edit(GenCode genCode, UserInfo userInfo) {
        GenCode genCodeInDb = genCodeRepository.findOne(genCode.getCodeId());
        if (genCodeInDb == null) {
            return RestfulRetUtils.getErrorMsg("51006", "当前编辑的对象不存在");
        }
        // 判断新表名是否已存在
        List<GenCode> list = genCodeRepository.findByTableName(genCode.getTableName());
        if (!genCode.getTableName().equals(genCodeInDb.getTableName()) && list != null && list.size() > 0) {
            return RestfulRetUtils.getErrorMsg("51006", "当前输入的表名已存在，请重新填写");
        }
        // 合并两个javabean
        BeanUtils.mergeBean(genCode, genCodeInDb);
        genCodeRepository.save(genCodeInDb);
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public JSONObject del(String idsList, UserInfo userInfo) {
        String[] ids = idsList.split(",");
        List<Long> idList = new ArrayList<Long>();
        // 判断是否需要移除
        for (int i = 0; i < ids.length; i++) {
            idList.add(StringUtils.toLong(ids[i]));
        }
        // 批量更新（设置IsValid 为N）
        if (idList.size() > 0) {
            genCodeRepository.delEntity(Constants.IS_VALID_INVALID, idList);
        }
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public GenCode getGenCode(Long codeId) {
        return genCodeRepository.getOne(codeId);
    }
}
