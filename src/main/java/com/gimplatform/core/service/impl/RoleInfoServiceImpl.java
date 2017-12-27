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
import com.gimplatform.core.entity.RoleInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.query.Criteria;
import com.gimplatform.core.query.CriteriaFactory;
import com.gimplatform.core.repository.FuncInfoRepository;
import com.gimplatform.core.repository.RoleInfoRepository;
import com.gimplatform.core.service.RoleInfoService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 角色信息服务类
 * @author zzd
 *
 */
@Service
public class RoleInfoServiceImpl implements RoleInfoService{

    @Autowired
    private RoleInfoRepository roleInfoRepository;

    @Autowired
    private FuncInfoRepository funcInfoRepository;

    /**
     * 根据用户code查找角色名称列表
     * @param userCode
     * @return
     */
    public List<String> getRolesNameByUser(UserInfo userInfo){
    	return roleInfoRepository.getUserRoleName(userInfo.getUserCode(), userInfo.getOrganizerId());
//    	List<String> list1 = roleInfoRepository.getRolesNameByUserCode(userInfo.getUserCode());
//    	List<String> list2 = roleInfoRepository.getRolesNameByOrganizerId(userInfo.getOrganizerId());
//    	//去重
//    	Set<String> set = new LinkedHashSet<>();
//        set.addAll(list1);
//        set.addAll(list2);
//        List<String> retList = new ArrayList<String>();
//        retList.addAll(set);
//        return retList;
    }

	@Override
	public Page<RoleInfo> getRoleList(Pageable page, RoleInfo roleInfo) {
		Criteria<RoleInfo> criteria = new Criteria<RoleInfo>();
		criteria.add(CriteriaFactory.like("roleName", roleInfo.getRoleName()));
		criteria.add(CriteriaFactory.equal("tenantsId", roleInfo.getTenantsId()));
		criteria.add(CriteriaFactory.equal("organizerId", roleInfo.getOrganizerId()));
		criteria.add(CriteriaFactory.equal("isValid", "Y"));
		return roleInfoRepository.findAll(criteria, new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.ASC, "roleId")));
	}

	@Override
	public JSONObject getRoleUserList(Pageable page, Map<String, Object> params) {
		List<Map<String, Object>> list = roleInfoRepository.getRoleUserList(params, page.getPageNumber(), page.getPageSize());
		int count = roleInfoRepository.getRoleUserListCount(params);
		return RestfulRetUtils.getRetSuccessWithPage(list, count);	
	}

	@Override
	public JSONObject getFuncTreeByRoleId(UserInfo userInfo, Long roleId) {
		List<Object> list = funcInfoRepository.getFuncTreeByRoleId(roleId);
		List<Long> listResult = new ArrayList<Long>();
		for(Object id : list){
			listResult.add(StringUtils.toLong(id, 0L));
		}
		return RestfulRetUtils.getRetSuccess(listResult);
	}

	@Override
	public JSONObject addRole(RoleInfo roleInfo, UserInfo userInfo) {
		if(roleInfo.getTenantsId() == null) roleInfo.setTenantsId(userInfo.getTenantsId());
		if(roleInfo.getOrganizerId() == null) roleInfo.setOrganizerId(userInfo.getOrganizerId());
		roleInfo.setIsValid(Constants.IS_VALID_VALID);
		if(roleInfoRepository.findByRoleNameAndTenantsIdAndOrganizerIdAndIsValid(roleInfo.getRoleName(),
				roleInfo.getTenantsId(), roleInfo.getOrganizerId(), Constants.IS_VALID_VALID).size() > 0){
			return RestfulRetUtils.getErrorMsg("25006","角色名称已存在");
		}
		roleInfo.setCreateDate(new Date());
		roleInfo.setCreateBy(userInfo.getUserId());
		roleInfo.setModifyBy(userInfo.getUserId());
		roleInfo.setModifyDate(new Date());
		roleInfoRepository.save(roleInfo);

		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject editRole(RoleInfo roleInfo, UserInfo userInfo) {
		if(roleInfo.getTenantsId() == null) roleInfo.setTenantsId(userInfo.getTenantsId());
		if(roleInfo.getOrganizerId() == null) roleInfo.setOrganizerId(userInfo.getOrganizerId());
		roleInfo.setIsValid(Constants.IS_VALID_VALID);
		RoleInfo roleInfoInDb = roleInfoRepository.findOne(roleInfo.getRoleId());
		if(roleInfoInDb == null){
			return RestfulRetUtils.getErrorMsg("25007","当前编辑的角色不存在");
		}
		if(roleInfoInDb != null && !roleInfoInDb.getRoleName().equals(roleInfo.getRoleName()) && 
				roleInfoRepository.findByRoleNameAndTenantsIdAndOrganizerIdAndIsValid(roleInfo.getRoleName(),
						roleInfo.getTenantsId(), roleInfo.getOrganizerId(), Constants.IS_VALID_VALID).size() > 0){
			return RestfulRetUtils.getErrorMsg("25006","角色名称已存在");
		}
		//合并两个javabean
		BeanUtils.mergeBean(roleInfo, roleInfoInDb);

		roleInfoInDb.setModifyBy(userInfo.getUserId());
		roleInfoInDb.setModifyDate(new Date());
		roleInfoRepository.save(roleInfoInDb);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject delRole(Long roleId, UserInfo userInfo) {
		RoleInfo roleInfo = roleInfoRepository.findOne(roleId);
		if(roleInfo != null){
			if(Constants.IS_VALID_VALID.equals(roleInfo.getIsFix())){
				return RestfulRetUtils.getErrorMsg("25016","不能删除固定的角色");
			}
			roleInfo.setIsValid(Constants.IS_VALID_INVALID);
			roleInfo.setModifyBy(userInfo.getUserId());
			roleInfo.setModifyDate(new Date());
			roleInfoRepository.save(roleInfo);
			//删除对应用户角色数据
			roleInfoRepository.delUserRoleByRoleId(roleInfo.getRoleId());
			//删除对应角色菜单数据
			roleInfoRepository.delRoleFuncByRoleId(roleInfo.getRoleId());
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject saveRoleFunc(UserInfo userInfo, Long roleId, List<Long> listFuncId) {
		//删除对应角色菜单数据
		roleInfoRepository.delRoleFuncByRoleId(roleId);
		for(Long funcId : listFuncId){
			roleInfoRepository.saveRoleFunc(roleId, funcId);
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject addUserRole(UserInfo userInfo, Long roleId, List<Long> userIdList) {
		for(Long userId : userIdList){
			roleInfoRepository.saveUserRole(userId, roleId);
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject delUserRole(UserInfo userInfo, Long roleId, List<Long> userIdList) {
		roleInfoRepository.delUserRoleByRoleIdAndUserId(roleId, userIdList);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject getRolesKeyValByOrganizerId(Long organizerId) {
		return RestfulRetUtils.getRetSuccess(roleInfoRepository.getRolesKeyValByOrganizerId(organizerId));
	}
}
