package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.TbGenerator;
import com.gimplatform.core.query.Criteria;
import com.gimplatform.core.query.CriteriaFactory;
import com.gimplatform.core.repository.TbGeneratorRepository;
import com.gimplatform.core.service.TbGeneratorService;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

@Service
public class TbGeneratorServiceImpl implements TbGeneratorService {

    @Autowired
    private TbGeneratorRepository tbGeneratorRepository;

    @Override
    public JSONObject getList(Pageable page, TbGenerator tbGenerator) {
        Criteria<TbGenerator> criteria = new Criteria<TbGenerator>();
        criteria.add(CriteriaFactory.like("genName", tbGenerator.getGenName()));
        Page<TbGenerator> list = tbGeneratorRepository.findAll(criteria, new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.ASC, "id")));
        List<TbGenerator> objList = list.getContent();
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> tmpMap = null;
        for (TbGenerator obj : objList) {
            String table = obj.getGenTable();
            String primaryKey = obj.getGenPrimaryKey();
            tmpMap = new HashMap<String, Object>();
            tmpMap.put("id", obj.getId());
            tmpMap.put("genTable", table);
            tmpMap.put("genPrimaryKey", primaryKey);
            tmpMap.put("genName", obj.getGenName());
            tmpMap.put("genValue", obj.getGenValue());
            if (!StringUtils.isBlank(table) && !StringUtils.isBlank(primaryKey)) {
                try {
                    String maxid = tbGeneratorRepository.getMaxId(primaryKey, table);
                    tmpMap.put("tableMaxValue", StringUtils.isBlank(maxid) ? "0" : maxid);
                } catch (Exception e) {
                    continue;
                }
            }
            mapList.add(tmpMap);
        }
        return RestfulRetUtils.getRetSuccessWithPage(mapList, list.getTotalElements());
    }

    @Override
    public JSONObject fixGenerator() {
        List<TbGenerator> objList = tbGeneratorRepository.findAll();
        for (TbGenerator obj : objList) {
            String table = obj.getGenTable();
            String primaryKey = obj.getGenPrimaryKey();
            // Long genValue = obj.getGenValue();
            if (!StringUtils.isBlank(table) && !StringUtils.isBlank(primaryKey)) {
                try {
                    Long maxValue = StringUtils.toLong(tbGeneratorRepository.getMaxId(primaryKey, table), 0L);
                    // 更新数据库
                    tbGeneratorRepository.updateGenValue(obj.getId(), maxValue + 1);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return RestfulRetUtils.getRetSuccess();
    }

    @Override
    public String getMaxId(String primaryKey, String table) {
        return tbGeneratorRepository.getMaxId(primaryKey, table);
    }

}
