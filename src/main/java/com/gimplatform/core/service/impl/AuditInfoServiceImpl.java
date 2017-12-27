package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.AuditInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.query.Criteria;
import com.gimplatform.core.query.CriteriaFactory;
import com.gimplatform.core.repository.AuditInfoRepository;
import com.gimplatform.core.service.AuditInfoService;
import com.gimplatform.core.service.UserInfoService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.JsonUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

@Service
public class AuditInfoServiceImpl implements AuditInfoService {

	@Autowired
	private AuditInfoRepository auditInfoRepository;

	@Autowired
	private UserInfoService userInfoService;

	@Override
	public Page<AuditInfo> getList(Pageable page, AuditInfo auditInfo) {
		Criteria<AuditInfo> criteria = new Criteria<AuditInfo>();
		criteria.add(CriteriaFactory.equal("auditType", auditInfo.getAuditType()));
		criteria.add(CriteriaFactory.equal("auditStatus", auditInfo.getAuditStatus()));
		criteria.add(CriteriaFactory.equal("editStatus", auditInfo.getEditStatus()));
		criteria.add(CriteriaFactory.equal("createBy", auditInfo.getCreateBy()));
		criteria.add(CriteriaFactory.equal("isValid", "Y"));
		return auditInfoRepository.findAll(criteria,
				new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.DESC, "id")));
	}

	@Override
	public JSONObject add(AuditInfo auditInfo, UserInfo userInfo) {
		auditInfo.setIsValid(Constants.IS_VALID_VALID);
		auditInfo.setCreateBy(userInfo.getUserId());
		auditInfo.setCreateDate(new Date());
		auditInfoRepository.save(auditInfo);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject edit(AuditInfo auditInfo, UserInfo userInfo) {
		AuditInfo auditInfoInDb = auditInfoRepository.findOne(auditInfo.getId());
		if (auditInfoInDb == null) {
			return RestfulRetUtils.getErrorMsg("51006", "当前编辑的内容不存在");
		}
		// 合并两个javabean
		BeanUtils.mergeBean(auditInfo, auditInfoInDb);
		auditInfoRepository.save(auditInfoInDb);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject del(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		//判断是否需要移除
		List<Long> idList = new ArrayList<Long>();
		for (int i = 0; i < ids.length; i++) {
			idList.add(StringUtils.toLong(ids[i]));
		}
		//批量更新（设置IsValid 为N）
		if(idList.size() > 0){
			auditInfoRepository.delEntity(Constants.IS_VALID_INVALID, idList);
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject auditUserPass(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		List<Long> idList = new ArrayList<Long>();
		Long id = null;
		for (int i = 0; i < ids.length; i++) {
			id = StringUtils.toLong(ids[i]);
			idList.add(id);
			AuditInfo auditInfo = auditInfoRepository.findOne(id);
			//根据不同的状态处理
			if(auditInfo == null) continue;
			if("1".equals(auditInfo.getEditStatus())){
				//新增
				if(!StringUtils.isBlank(auditInfo.getEditCache())){
					JSONObject tmpJson = JSONObject.parseObject(auditInfo.getEditCache());
					if(tmpJson != null){
						Map<String, Object> tmpMap = JsonUtils.jsonToMap(tmpJson);
						UserInfo newUserInfo = (UserInfo) BeanUtils.mapToBean(tmpMap, UserInfo.class);
						userInfoService.addUser(newUserInfo, null, null, null);
					}
				}
				//更改状态
				auditInfo.setAuditStatus("4");
				auditInfoRepository.save(auditInfo);
			}else if("2".equals(auditInfo.getEditStatus())){
				
			}else if("3".equals(auditInfo.getEditStatus())){
				
			}
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject auditNotPass(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		List<Long> idList = new ArrayList<Long>();
		for (int i = 0; i < ids.length; i++) {
			idList.add(StringUtils.toLong(ids[i]));
		}
		if(idList.size() > 0){
			auditInfoRepository.auditNotPass(idList);
		}
		return RestfulRetUtils.getRetSuccess();
	}

}
