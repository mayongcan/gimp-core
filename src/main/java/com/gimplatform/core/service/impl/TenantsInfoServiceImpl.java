package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.TenantsInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.query.Criteria;
import com.gimplatform.core.query.CriteriaFactory;
import com.gimplatform.core.repository.TenantsInfoRepository;
import com.gimplatform.core.service.OrganizerInfoService;
import com.gimplatform.core.service.TenantsInfoService;
import com.gimplatform.core.service.UserInfoService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 租户信息服务类
 * @author zzd
 *
 */
@Service
public class TenantsInfoServiceImpl implements TenantsInfoService{

    @Autowired
    private TenantsInfoRepository tenantsInfoRepository;

    @Autowired
	private OrganizerInfoService organizerInfoService;

    @Autowired
    private UserInfoService userInfoService;

	@Override
	public TenantsInfo getByTenantsId(Long tenantsId) {
		return tenantsInfoRepository.findOne(tenantsId);
	}

	@Override
	public JSONObject getAllTenantsList() {
		List<TenantsInfo> tenantsList = tenantsInfoRepository.findByIsValid(Constants.IS_VALID_VALID);
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		for(TenantsInfo tenantsInfo : tenantsList){
			if(tenantsInfo != null){
				map = new HashMap<String, Object>();
				map.put("tenantsId", tenantsInfo.getTenantsId());
				map.put("tenantsName", tenantsInfo.getTenantsName());
				mapList.add(map);
			}
		}
		return RestfulRetUtils.getRetSuccess(mapList);
	}

	public Page<TenantsInfo> getTenantsList(Pageable page, final TenantsInfo tenantsInfo) {
		Criteria<TenantsInfo> criteria = new Criteria<TenantsInfo>();
		criteria.add(CriteriaFactory.like("tenantsName", tenantsInfo.getTenantsName()));
		criteria.add(CriteriaFactory.equal("status", tenantsInfo.getStatus()));
		criteria.add(CriteriaFactory.equal("isValid", Constants.IS_VALID_VALID));
		return tenantsInfoRepository.findAll(criteria, new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.ASC, "tenantsId")));
	}

	@Override
	public JSONObject addTenants(TenantsInfo tenantsInfo, UserInfo userInfo) {
		tenantsInfo.setIsValid(Constants.IS_VALID_VALID);
		tenantsInfo.setIsRoot(Constants.IS_VALID_INVALID);
		if(tenantsInfoRepository.findByTenantsNameAndIsValid(tenantsInfo.getTenantsName(), Constants.IS_VALID_VALID) != null){
			return RestfulRetUtils.getErrorMsg("21005","当前租户名称已存在");
		}
		tenantsInfo.setCreateDate(new Date());
		tenantsInfo.setCreateBy(userInfo.getUserId());
		tenantsInfo.setModifyBy(userInfo.getUserId());
		tenantsInfo.setModifyDate(new Date());
		tenantsInfoRepository.save(tenantsInfo);
		//创建一个根组织节点
		organizerInfoService.addOrganizerByTenants(tenantsInfo, userInfo);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject editTenants(TenantsInfo tenantsInfo, UserInfo userInfo) {
		TenantsInfo tenantsInDb = tenantsInfoRepository.findOne(tenantsInfo.getTenantsId());
		if(tenantsInDb == null){
			return RestfulRetUtils.getErrorMsg("21006","当前编辑的租户不存在");
		}
		if(tenantsInDb != null && !tenantsInDb.getTenantsName().equals(tenantsInfo.getTenantsName()) && 
				tenantsInfoRepository.findByTenantsNameAndIsValid(tenantsInfo.getTenantsName(), Constants.IS_VALID_VALID) != null){
			return RestfulRetUtils.getErrorMsg("21005","当前租户名称已存在");
		}
		//合并两个javabean
		BeanUtils.mergeBean(tenantsInfo, tenantsInDb);

		tenantsInDb.setModifyBy(userInfo.getUserId());
		tenantsInDb.setModifyDate(new Date());
		tenantsInfoRepository.save(tenantsInDb);
		//修改根组织
		organizerInfoService.editOrganizerByTenants(tenantsInDb, userInfo);
		
		//如果修改了时间，则将所有属于该租户的组织和用户的时间一并修改
		organizerInfoService.updateBeginDateByTenantsId(tenantsInDb.getTenantsId(), tenantsInDb.getBeginDate());
		userInfoService.updateBeginDateByTenantsId(tenantsInDb.getTenantsId(), tenantsInDb.getBeginDate());
		organizerInfoService.updateEndDateByTenantsId(tenantsInDb.getTenantsId(), tenantsInDb.getEndDate());
		userInfoService.updateEndDateByTenantsId(tenantsInDb.getTenantsId(), tenantsInDb.getEndDate());

		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject delTenants(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		List<Long> idList = new ArrayList<Long>();
		TenantsInfo tenants = null;
		//判断是否需要移除
		for (int i = 0; i < ids.length; i++) {
			tenants = tenantsInfoRepository.getOne(StringUtils.toLong(ids[i]));
			if(tenants != null && Constants.IS_VALID_VALID.equals(tenants.getIsValid())){
				idList.add(StringUtils.toLong(ids[i]));
			}
			if(tenants != null && tenants.getIsRoot().equals(Constants.IS_VALID_VALID)){
				return RestfulRetUtils.getErrorMsg("21006","不能删除根租户[" + tenants.getTenantsName() + "]");
			}
		}
		//批量更新（设置IsValid 为N）
		if(idList.size() > 0)
			tenantsInfoRepository.delTenants(Constants.IS_VALID_INVALID, userInfo.getUserId(), new Date(), idList);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject saveTenantsFunc(UserInfo userInfo, Long tenantsId, List<Long> listFuncId) {
		tenantsInfoRepository.delTenantsFuncByTenantsId(tenantsId);
		for(Long funcId : listFuncId){
			tenantsInfoRepository.saveTenantsFunc(tenantsId, funcId);
		}
		return RestfulRetUtils.getRetSuccess();
	}
    
}
