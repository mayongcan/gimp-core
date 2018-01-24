package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.OrganizerRecur;
import com.gimplatform.core.entity.RoleInfo;
import com.gimplatform.core.entity.DataPermission;
import com.gimplatform.core.entity.OrganizerInfo;
import com.gimplatform.core.entity.TenantsInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.query.Criteria;
import com.gimplatform.core.query.CriteriaFactory;
import com.gimplatform.core.repository.DataPermissionRepository;
import com.gimplatform.core.repository.OrganizerInfoRepository;
import com.gimplatform.core.repository.RoleInfoRepository;
import com.gimplatform.core.repository.UserInfoRepository;
import com.gimplatform.core.service.OrganizerInfoService;
import com.gimplatform.core.service.UserInfoService;
import com.gimplatform.core.tree.Tree;
import com.gimplatform.core.tree.TreeNode;
import com.gimplatform.core.tree.TreeNodeExtend;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.PinyinUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.SessionUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 组织信息服务类
 * @author zzd
 *
 */
@Service
public class OrganizerInfoServiceImpl implements OrganizerInfoService{

    @Autowired
    private OrganizerInfoRepository organizerInfoRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RoleInfoRepository roleInfoRepository;

    @Autowired
    private DataPermissionRepository dataPermissionRepository;

	@Override
	public OrganizerInfo getByOrganizerId(Long organizerId) {
		if(organizerId == null) return null;
		else return organizerInfoRepository.findOne(organizerId);
	}

	public JSONArray getOrganizerTreeByParentId(Map<String, Object> params) {
		Long organizerId = MapUtils.getLong(params, "organizerId");
		List<Map<String, Object>> orgList = organizerInfoRepository.getOrganizerTree(params);
		//如果是根组织，则根设置为null
		if(organizerId != null && organizerId.equals(SessionUtils.getUserRootOrganizerId())) organizerId = null;
		return getJsonTree(orgList, organizerId);
	}

	public JSONArray getOrganizerTreeById(UserInfo userInfo, Map<String, Object> params) {
		Long parentOrgId = MapUtils.getLong(params, "parentOrgId");
		Long organizerId = MapUtils.getLong(params, "organizerId");
		if(parentOrgId == null || parentOrgId.equals(-1L)) {
			if(organizerId == null  || organizerId.equals(-1L)) 
				organizerId = userInfo.getOrganizerId();
			List<Map<String, Object>> orgList = organizerInfoRepository.getOrganizerTree(params);
			//如果是根组织，则根设置为null
			if(organizerId != null && organizerId.equals(SessionUtils.getUserRootOrganizerId())) organizerId = null;
			return getJsonTree(orgList, organizerId);
		}
		else 
			return getOrganizerTreeByParentId(params);
	}

	public JSONArray getOrganizerTreeByTenantsId(UserInfo userInfo, Map<String, Object> params) {
		Long parentOrgId = MapUtils.getLong(params, "parentOrgId");
		Long organizerId = MapUtils.getLong(params, "organizerId");
		if(parentOrgId == null || parentOrgId.equals(-1L)) {
			List<Map<String, Object>> orgList = organizerInfoRepository.getOrganizerTree(params);
			//如果是根组织，则根设置为null
			if(organizerId != null && organizerId.equals(SessionUtils.getUserRootOrganizerId())) organizerId = null;
			return getJsonTree(orgList, organizerId);
		}
		else 
			return getOrganizerTreeByParentId(params);
	}

	@Override
	public void addOrganizerByTenants(TenantsInfo tenantsInfo, UserInfo userInfo) {
		OrganizerInfo organizerInfo = new OrganizerInfo();
		organizerInfo.setOrganizerName(tenantsInfo.getTenantsName() + "-根组织");
		organizerInfo.setOrganizerMemo(tenantsInfo.getTenantsName() + "-根组织");
		organizerInfo.setNameFirstLetter(PinyinUtils.converterToFirstSpell(organizerInfo.getOrganizerName()));
		organizerInfo.setNameFullLetter(PinyinUtils.converterToSpell(organizerInfo.getOrganizerName()));
		organizerInfo.setOrganizerType(1);
		organizerInfo.setTenantsId(tenantsInfo.getTenantsId());
		organizerInfo.setStatus(1L);
		organizerInfo.setBeginDate(tenantsInfo.getBeginDate());
		organizerInfo.setEndDate(tenantsInfo.getEndDate());
		organizerInfo.setMaxUsers(tenantsInfo.getMaxUsers());
		organizerInfo.setIsValid(Constants.IS_VALID_VALID);

		organizerInfo.setCreateDate(new Date());
		organizerInfo.setCreateBy(userInfo.getUserId());
		organizerInfo.setModifyBy(userInfo.getUserId());
		organizerInfo.setModifyDate(new Date());
		
		organizerInfoRepository.saveAndFlush(organizerInfo);
		//保存sys_organizer_recur
		addOrganizerRecur(organizerInfo);
	}

	@Override
	public void editOrganizerByTenants(TenantsInfo tenantsInfo, UserInfo userInfo) {
		OrganizerInfo organizerInfo = organizerInfoRepository.getOrganizerRootByTenantsId(tenantsInfo.getTenantsId());
		if(organizerInfo != null){
			organizerInfo.setBeginDate(tenantsInfo.getBeginDate());
			organizerInfo.setEndDate(tenantsInfo.getEndDate());
			organizerInfo.setMaxUsers(tenantsInfo.getMaxUsers());
			organizerInfo.setModifyBy(userInfo.getUserId());
			organizerInfo.setModifyDate(new Date());
			
			organizerInfoRepository.save(organizerInfo);
		}
	}

	@Override
	public JSONObject addOrganizer(OrganizerInfo organizerInfo, UserInfo userInfo, Map<String, Object> params) {
		organizerInfo.setIsValid(Constants.IS_VALID_VALID);
		if(organizerInfo.getTenantsId() == null){
			organizerInfo.setTenantsId(userInfo.getTenantsId());
		}
		if(organizerInfo.getOrganizerType() == null){
			organizerInfo.setOrganizerType(2);
		}
		organizerInfo.setNameFirstLetter(PinyinUtils.converterToFirstSpell(organizerInfo.getOrganizerName()));
		organizerInfo.setNameFullLetter(PinyinUtils.converterToSpell(organizerInfo.getOrganizerName()));

		if(organizerInfoRepository.findByOrganizerNameAndIsValidAndTenantsId(organizerInfo.getOrganizerName(), Constants.IS_VALID_VALID, userInfo.getTenantsId()).size() > 0){
			return RestfulRetUtils.getErrorMsg("23005","当前组织机构名称已存在");
		}
//		//部门节点下禁止添加公司节点
//		if(IsParentDept(organizerInfo)){
//			return RestfulRetUtils.getErrorMsg("23006","无法在部门节点下添加组织类型为公司的节点");
//		}
//		//岗位下禁止再建立组织
//		if(IsParentPost(organizerInfo)){
//			return RestfulRetUtils.getErrorMsg("23006","无法在岗位节点下添加任何新节点");
//		}

		//除根组织，所有的组织MaxUsers设为0
		if(organizerInfo.getParentOrgId() != null) organizerInfo.setMaxUsers(0L);
		organizerInfo.setCreateDate(new Date());
		organizerInfo.setCreateBy(userInfo.getUserId());
		organizerInfo.setModifyBy(userInfo.getUserId());
		organizerInfo.setModifyDate(new Date());
		organizerInfo = organizerInfoRepository.saveAndFlush(organizerInfo);
		//保存sys_organizer_recur
		addOrganizerRecur(organizerInfo);

		//判断是否添加默认的角色和数据权限
		Long roleId = MapUtils.getLong(params, "roleId", null);
		Long permissionId = MapUtils.getLong(params, "permissionId", null);
		if(roleId != null){
			roleInfoRepository.saveOrganizerRole(organizerInfo.getOrganizerId(), roleId);
		}
		if(permissionId != null){
			dataPermissionRepository.saveOrganizerDataPermission(organizerInfo.getOrganizerId(), permissionId);
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject editOrganizer(OrganizerInfo organizerInfo, UserInfo userInfo, Map<String, Object> params) {
		organizerInfo.setIsValid(Constants.IS_VALID_VALID);
		if(!StringUtils.isBlank(organizerInfo.getOrganizerName())){
			organizerInfo.setNameFirstLetter(PinyinUtils.converterToFirstSpell(organizerInfo.getOrganizerName()));
			organizerInfo.setNameFullLetter(PinyinUtils.converterToSpell(organizerInfo.getOrganizerName()));
		}
		
		OrganizerInfo organizerInDb = organizerInfoRepository.findOne(organizerInfo.getOrganizerId());
		if(organizerInDb == null){
			return RestfulRetUtils.getErrorMsg("23007","当前编辑的组织机构不存在");
		}
		if(organizerInDb != null && !organizerInDb.getOrganizerName().equals(organizerInfo.getOrganizerName()) && 
				organizerInfoRepository.findByOrganizerNameAndIsValidAndTenantsId(organizerInfo.getOrganizerName(), Constants.IS_VALID_VALID, userInfo.getTenantsId()).size() > 0){
			return RestfulRetUtils.getErrorMsg("23005","当前组织机构名称已存在");
		}
		if(organizerInDb != null && !organizerInDb.getOrganizerType().equals(organizerInfo.getOrganizerType()) && 
				userInfoRepository.findByOrganizerIdAndIsValid(organizerInfo.getOrganizerId(), Constants.IS_VALID_VALID).size() > 0){
			return RestfulRetUtils.getErrorMsg("23008","组织节点下还有用户，无法变更组织类型");
		}
		//判断变更的组织父ID是否是自己的子组织
		if(organizerInDb != null && organizerInDb.getParentOrgId() != null && 
				organizerInfo.getParentOrgId() != null && !organizerInDb.getParentOrgId().equals(organizerInfo.getParentOrgId())){
			List<Object> childIdList = organizerInfoRepository.getAllChildIdByOrganizerId(organizerInDb.getOrganizerId());
			for(Object id : childIdList){
				if(organizerInfo.getParentOrgId().equals(StringUtils.toLong(id))){
					return RestfulRetUtils.getErrorMsg("23009","不能将父组织节点更改为下属节点！");
				}
			}
//			//判断更换的父组织节点是否为部门节点
//			if(IsParentDept(organizerInfo)){
//				return RestfulRetUtils.getErrorMsg("23006","无法将公司节点切换到部门节点下！");
//			}
//			//岗位下禁止再建立组织
//			if(IsParentPost(organizerInfo)){
//				return RestfulRetUtils.getErrorMsg("23006","无法在岗位节点下添加任何新节点");
//			}
		}
		
//		boolean updateOrgExt = true;
//		if(organizerInDb != null && organizerInDb.getParentOrgId() != null && organizerInDb.getParentOrgId().equals(organizerInfo.getParentOrgId()) &&
//				organizerInDb.getOrganizerName().equals(organizerInfo.getOrganizerName())){
//			updateOrgExt = false;
//		}

		//合并两个javabean
		BeanUtils.mergeBean(organizerInfo, organizerInDb);

		if(organizerInDb.getTenantsId() == null){
			organizerInDb.setTenantsId(userInfo.getTenantsId());
		}
		if(organizerInDb.getOrganizerType() == null){
			organizerInDb.setOrganizerType(2);
		}
		
		if(organizerInDb.getParentOrgId() != null) organizerInDb.setMaxUsers(0L);
		organizerInDb.setModifyBy(userInfo.getUserId());
		organizerInDb.setModifyDate(new Date());
		organizerInfo = organizerInfoRepository.saveAndFlush(organizerInDb);
		//保存sys_organizer_recur
		 addOrganizerRecur(organizerInfo);

		//判断是否添加默认的角色和数据权限
		Long roleId = MapUtils.getLong(params, "roleId", null);
		Long permissionId = MapUtils.getLong(params, "permissionId", null);
		roleInfoRepository.delOrganizerRoleByOrganizerId(organizerInfo.getOrganizerId());
		dataPermissionRepository.delOrganizerDataPermissionByOrganizerId(organizerInfo.getOrganizerId());
		if(roleId != null){
			roleInfoRepository.saveOrganizerRole(organizerInfo.getOrganizerId(), roleId);
		}
		if(permissionId != null){
			dataPermissionRepository.saveOrganizerDataPermission(organizerInfo.getOrganizerId(), permissionId);
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject delOrganizer(String idsList, UserInfo userInfo) {
		String[] ids = idsList.split(",");
		List<Long> idList = new ArrayList<Long>();
		//判断是否需要移除
		int userSize = 0;
		OrganizerInfo organizerInfo = null;
		for (int i = 0; i < ids.length; i++) {
			organizerInfo = organizerInfoRepository.getOne(StringUtils.toLong(ids[i]));
			if(organizerInfo == null) continue;
			userSize = userInfoRepository.findByOrganizerIdAndIsValid(organizerInfo.getOrganizerId(), Constants.IS_VALID_VALID).size();
			if(userSize > 0){
				return RestfulRetUtils.getErrorMsg("23008","组织节点下还有用户，无法删除组织，请检查组织数据！");
			}
			if(organizerInfo != null && Constants.IS_VALID_VALID.equals(organizerInfo.getIsValid()) && userSize == 0){
				idList.add(StringUtils.toLong(ids[i]));
			}
		}

		//批量更新
		if(idList.size() > 0){
			organizerInfoRepository.delOrganizer(Constants.IS_VALID_INVALID, userInfo.getUserId(), new Date(), idList);
			List<OrganizerInfo> list = organizerInfoRepository.getOrganizerByParentOrgId(Constants.IS_VALID_INVALID, idList);
			setChildrensInvalid(list, userInfo);
		}
		return RestfulRetUtils.getRetSuccess();
	}
	
//	/**
//	 * 检测是否在部门下添加节点
//	 * @param organizerInfo
//	 * @return
//	 */
//	private boolean IsParentDept(OrganizerInfo organizerInfo){
//		if(organizerInfo.getParentOrgId() == null) return false;
//		OrganizerInfo orgInfo = organizerInfoRepository.findOne(organizerInfo.getParentOrgId());
//		//判断新节点是否为部门节点，同时新节点是否为公司节点
//		if(orgInfo == null) return true;
//		else if(orgInfo.getOrganizerType() == 2 && organizerInfo.getOrganizerType() == 1) return true;
//		else return false;
//	}
//	
//	/**
//	 * 检查是否在岗位下添加节点
//	 * @param organizerInfo
//	 * @return
//	 */
//	private boolean IsParentPost(OrganizerInfo organizerInfo){
//		if(organizerInfo.getParentOrgId() == null) return false;
//		OrganizerInfo orgInfo = organizerInfoRepository.findOne(organizerInfo.getParentOrgId());
//		if(orgInfo == null) return true;
//		else if(orgInfo.getOrganizerType() == 3) return true;
//		else return false;
//	}

	/**
	 * 设置子节点不可用(递归设置)
	 * @param orgList
	 * @param userInfo
	 */
	private void setChildrensInvalid(List<OrganizerInfo> orgList, UserInfo userInfo) {
		OrganizerInfo organizer = null;
		List<OrganizerInfo> childrens = null;
		for (int i = 0; i < orgList.size(); i++) {
			organizer = (OrganizerInfo) orgList.get(i);
			childrens = organizerInfoRepository.findByParentOrgIdAndIsValid(organizer.getOrganizerId(), Constants.IS_VALID_VALID);
			if (childrens.size() != 0) setChildrensInvalid(childrens, userInfo);

			organizer.setIsValid(Constants.IS_VALID_INVALID);
			organizer.setModifyDate(new Date());
			organizer.setModifyBy(userInfo.getUserId());
			organizerInfoRepository.save(organizer);
		}
	}
	
	private void addOrganizerRecur(OrganizerInfo organizerInfo) {
		String idPath = organizerInfo.getOrganizerId() + "", namePath = organizerInfo.getOrganizerName();
		organizerInfoRepository.delOrganizerRecurByOrganizerChildId(organizerInfo.getOrganizerId());
		//保存当前节点信息
		OrganizerRecur orgExt = new OrganizerRecur();
		orgExt.setOrganizerId(organizerInfo.getOrganizerId());
		orgExt.setOrganizerChildId(organizerInfo.getOrganizerId());
		organizerInfoRepository.delOrganizerRecur(orgExt.getOrganizerId(), orgExt.getOrganizerChildId());
		organizerInfoRepository.saveOrganizerRecur(orgExt.getOrganizerId(), orgExt.getOrganizerChildId());
		
		Long orgParentId = organizerInfo.getParentOrgId();
		if(orgParentId != null){
			int deep=0;
			while(orgParentId != null && deep< Constants.DEFAULT_TREE_DEEP){
				idPath = orgParentId + "," + idPath;
				OrganizerInfo orgInfoParent = organizerInfoRepository.findOne(orgParentId);
				if(orgInfoParent != null){
					orgExt = new OrganizerRecur();
					orgExt.setOrganizerId(orgInfoParent.getOrganizerId());
					orgExt.setOrganizerChildId(organizerInfo.getOrganizerId());
					orgParentId = orgInfoParent.getParentOrgId();
					namePath = orgInfoParent.getOrganizerName() + ">>" + namePath;
					organizerInfoRepository.delOrganizerRecur(orgExt.getOrganizerId(), orgExt.getOrganizerChildId());
					organizerInfoRepository.saveOrganizerRecur(orgExt.getOrganizerId(), orgExt.getOrganizerChildId());
				}else{
					orgParentId = null;
				}
				deep++;
			}
		}
		
		organizerInfo.setIdPath(idPath);
		organizerInfo.setNamePath(namePath);
		organizerInfoRepository.save(organizerInfo);
	}

	@Override
	public Long getOrganizerByIdAndType(Long orgId, int type) {
		Long organizerId = null;
		int deep = 0;
		if(orgId != null){
			OrganizerInfo organizerInfo = organizerInfoRepository.findOne(orgId);
			if(organizerInfo != null){
				if(organizerInfo.getOrganizerType() == type){
					organizerId = organizerInfo.getOrganizerId();
				}else{
					while(organizerInfo.getOrganizerType() != type && deep < Constants.DEFAULT_TREE_DEEP){
						organizerInfo = organizerInfoRepository.findOne(organizerInfo.getParentOrgId());
						deep++;
					}
					if(organizerInfo.getOrganizerType() == type){
						organizerId = organizerInfo.getOrganizerId();
					}
				}
			}
		}
		return organizerId;
	}

	@Override
	public Long getOrganizerParentId(Long organizerId) {
		OrganizerInfo organizerInfo = organizerInfoRepository.getOne(organizerId);
		if(organizerInfo != null) return organizerInfo.getParentOrgId();
		else return -1L;
	}

	@Override
	public void updateBeginDateByTenantsId(Long tenantsId, Date beginDate) {
		organizerInfoRepository.updateBeginDateByTenantsId(tenantsId, beginDate);
	}

	@Override
	public void updateEndDateByTenantsId(Long tenantsId, Date endDate) {
		organizerInfoRepository.updateEndDateByTenantsId(tenantsId, endDate);
	}
	

	/**
	 * 获取json格式的树
	 * @param listFunc
	 * @param parentId
	 * @return
	 */
	private JSONArray getJsonTree(List<Map<String, Object>> listFunc, Long parentId){
		TreeNode root = new TreeNode("root", "all", null, false);
		if(parentId != null) root = new TreeNode(parentId.toString(), "all", null, false);
		root.setIcon("fa fa-sitemap");
		Map<String,String> mapAttr = null;
		TreeNodeExtend treeNode = null;
		String id = "", text= "", parent = "";
		Tree tree = new Tree(true);
		for(Map<String, Object> mapObj : listFunc){
			id = MapUtils.getString(mapObj, "organizerId", "");
			text = MapUtils.getString(mapObj, "organizerName", "");
			parent = MapUtils.getString(mapObj, "parentOrgId", "");
			//当获取到的根ID不为null，则将传送过来的parentId作为rootId
			if(parentId != null && parentId.equals(StringUtils.toLong(id))){
				parent = parentId.toString();
			}
			
			mapAttr = new HashMap<String,String>();
			String organizerType = MapUtils.getString(mapObj, "organizerType");
			mapAttr.put("organizerType", organizerType);
			//mapAttr.put("parent", MapUtils.getString(mapObj, "parentOrgId", ""));
			mapAttr.put("organizerMemo", MapUtils.getString(mapObj, "organizerMemo", ""));
			mapAttr.put("status", MapUtils.getString(mapObj, "status", ""));
			mapAttr.put("maxUsers", MapUtils.getString(mapObj, "maxUsers", ""));
			mapAttr.put("beginDate", MapUtils.getString(mapObj, "beginDate", ""));
			mapAttr.put("endDate", MapUtils.getString(mapObj, "endDate", ""));
			mapAttr.put("idPath", MapUtils.getString(mapObj, "idPath", ""));
			mapAttr.put("manager", MapUtils.getString(mapObj, "manager", ""));
			mapAttr.put("managerTel", MapUtils.getString(mapObj, "managerTel", ""));
			mapAttr.put("address", MapUtils.getString(mapObj, "address", ""));
			mapAttr.put("organizerCode", MapUtils.getString(mapObj, "organizerCode", ""));
			mapAttr.put("areaCode", MapUtils.getString(mapObj, "areaCode", ""));
			mapAttr.put("areaName", MapUtils.getString(mapObj, "areaName", ""));
			mapAttr.put("organizerLevel", MapUtils.getString(mapObj, "organizerLevel", ""));
			mapAttr.put("organizerFunc", MapUtils.getString(mapObj, "organizerFunc", ""));
			mapAttr.put("principle", MapUtils.getString(mapObj, "principle", ""));
			mapAttr.put("principleTel", MapUtils.getString(mapObj, "principleTel", ""));
			mapAttr.put("email", MapUtils.getString(mapObj, "email", ""));
			mapAttr.put("fax", MapUtils.getString(mapObj, "fax", ""));
			mapAttr.put("auditStatus", MapUtils.getString(mapObj, "auditStatus", ""));
			mapAttr.put("editStatus", MapUtils.getString(mapObj, "editStatus", ""));
			mapAttr.put("editCache", MapUtils.getString(mapObj, "editCache", ""));

			treeNode = new TreeNodeExtend(id, text, parent, false, mapAttr);
			
			//设置图标
			if("1".equals(organizerType))
				treeNode.setIcon("fa fa-building");
			else if("2".equals(organizerType))
				treeNode.setIcon("fa fa-cubes");
			else if("3".equals(organizerType))
				treeNode.setIcon("fa fa-address-card-o");
			else if("4".equals(organizerType))
				treeNode.setIcon("fa fa-university");
			else if("5".equals(organizerType))
				treeNode.setIcon("fa fa-bars");
			//设置根节点图标
			if(StringUtils.isBlank(parent)){
				treeNode.setIcon("fa fa-sitemap");
			}
			tree.addNode(treeNode);
		}
		String strTree = tree.getTreeJson(tree, root);
		//System.out.println(strTree);
		return JSONArray.parseArray(strTree);
	}

	/**
	 * 获取组织管理员ID
	 */
	@Override
	public Long getOrganizerManagerIdByUser(UserInfo userInfo) {
		Long id = userInfo.getOrganizerId();
		//获取用户的上级部门，如果用户当前的organizerId为岗位，则需要向上查找);
//		OrganizerInfo organizerInfo = null;
//		if(id != null){
//			organizerInfo = organizerInfoRepository.findOne(id);
//			int deep = 0;
//			while(organizerInfo != null && organizerInfo.getOrganizerType() == 3 && deep < Constants.DEFAULT_TREE_DEEP){
//				organizerInfo = organizerInfoRepository.findOne(organizerInfo.getParentOrgId());
//				deep++;
//			}
//		}
		//暂时取消向上查找，直接获取当前用户所在的组织的管理员(因为组织)
		OrganizerInfo organizerInfo = organizerInfoRepository.findOne(id);
		if(organizerInfo == null || StringUtils.isBlank(organizerInfo.getManager())) return null;
		else {
			//manager的格式userId + char(0) + userCode + char(0) + userName
			List<String> managerList = StringUtils.splitToList(organizerInfo.getManager(), Constants.SPLIT_CHAR_0, 3);
			Long userId = StringUtils.toLong(managerList.get(0), null);
			if(userId == null) return null;
			UserInfo tmpObj = userInfoRepository.findOne(userId);
			if(tmpObj == null) return null;
			else return tmpObj.getUserId();
		}
	}
	
	public OrganizerInfo getRootOrgByTenantsId(Long tenantsId){
		return organizerInfoRepository.getOrganizerRootByTenantsId(tenantsId);
	}

	@Override
	public JSONObject getRoleAndData(Long organizerId) {
		List<Object> roleIdList = roleInfoRepository.getRoleIdByOrganizerId(organizerId);
		List<Object> permissionIdList = dataPermissionRepository.getPermissionIdByOrganizerId(organizerId);
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(roleIdList != null && roleIdList.size() > 0) retMap.put("roleId", roleIdList.get(0));
		if(permissionIdList != null && permissionIdList.size() > 0) retMap.put("permissionId", permissionIdList.get(0));
		return RestfulRetUtils.getRetSuccess(retMap);
	}
	
	@Override
	public JSONObject getExtraInfo(Long organizerId, Long parentOrgId) {
		List<OrganizerInfo> parentOrg = organizerInfoRepository.findByParentOrgIdAndIsValid(parentOrgId, "Y");
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(parentOrg.size() > 0) retMap.put("parentOrgName", parentOrg.get(0).getOrganizerName());
		List<Object> roleIdList = roleInfoRepository.getRoleIdByOrganizerId(organizerId);
		List<Object> permissionIdList = dataPermissionRepository.getPermissionIdByOrganizerId(organizerId);
		if(roleIdList != null && roleIdList.size() > 0) {
			RoleInfo roleInfo = roleInfoRepository.getOne(StringUtils.toLong(roleIdList.get(0)));
			if(roleInfo != null) retMap.put("roleName", roleInfo.getRoleName());
		}
		if(permissionIdList != null && permissionIdList.size() > 0) {
			DataPermission dataPermission = dataPermissionRepository.findOne(StringUtils.toLong(permissionIdList.get(0)));
			retMap.put("permissionName", dataPermission.getPermissionName());
		}
		return RestfulRetUtils.getRetSuccess(retMap);
	}

	public Page<OrganizerInfo> getOrgainzerList(Pageable page, OrganizerInfo organizerInfo) {
		Criteria<OrganizerInfo> criteria = new Criteria<OrganizerInfo>();
		criteria.add(CriteriaFactory.like("organizerName", organizerInfo.getOrganizerName()));
		criteria.add(CriteriaFactory.equal("auditStatus", organizerInfo.getAuditStatus()));
		criteria.add(CriteriaFactory.equal("isValid", Constants.IS_VALID_VALID));
		return organizerInfoRepository.findAll(criteria, new PageRequest(page.getPageNumber(), page.getPageSize(), new Sort(Direction.ASC, "organizerId")));
	}
	
	public JSONObject submitOrganizerCache(OrganizerInfo organizerInfo, UserInfo userInfo, Map<String, Object> params) {
		OrganizerInfo organizerInDb = organizerInfoRepository.findOne(organizerInfo.getOrganizerId());
		if(organizerInDb == null){
			return RestfulRetUtils.getErrorMsg("23007","当前编辑的组织机构不存在");
		}
		if(organizerInDb != null && !organizerInDb.getOrganizerName().equals(organizerInfo.getOrganizerName()) && 
				organizerInfoRepository.findByOrganizerNameAndIsValidAndTenantsId(organizerInfo.getOrganizerName(), Constants.IS_VALID_VALID, userInfo.getTenantsId()).size() > 0){
			return RestfulRetUtils.getErrorMsg("23005","当前组织机构名称已存在");
		}

		//合并两个javabean
		BeanUtils.mergeBean(organizerInfo, organizerInDb);
		organizerInfo = organizerInfoRepository.saveAndFlush(organizerInDb);
		return RestfulRetUtils.getRetSuccess(organizerInfo);
	}

	@Override
	public JSONObject auditPass(String idsList, UserInfo userInfo) {
		//审核通过，如果是新增，则更改状态，如果是编辑则将缓存状态写入最新值，如果是删除，则删除内容
		String[] ids = idsList.split(",");
		OrganizerInfo organizerInfo = null;
		String delimiter = Constants.SPLIT_CHAR_1;
		for (int i = 0; i < ids.length; i++) {
			Long id = StringUtils.toLong(ids[i], null);
			if(id != null){
				organizerInfo = organizerInfoRepository.findOne(id);
				if("1".equals(organizerInfo.getEditStatus())){
					organizerInfo.setAuditStatus("4");
					organizerInfo.setEditStatus("");
					//注册的时候，将管理员信息写入editCache，所以需要取出来，同时写入UserInfo表
					List<String> cacheList = StringUtils.splitToList(organizerInfo.getEditCache(), Constants.SPLIT_CHAR_0, 3);
					UserInfo manager = new UserInfo();
					manager.setUserCode(cacheList.get(0));
					manager.setUserName(cacheList.get(1));
					manager.setPassword(cacheList.get(2));
					manager.setTenantsId(organizerInfo.getTenantsId());
					manager.setOrganizerId(organizerInfo.getOrganizerId());
					JSONObject tmpJson = userInfoService.addUser(manager, userInfo, null, null);
					if(tmpJson != null){
						manager = (UserInfo)tmpJson.get("RetData");
					}

					organizerInfo.setManager(manager.getUserId() + Constants.SPLIT_CHAR_0 + cacheList.get(0) + Constants.SPLIT_CHAR_0 + cacheList.get(1));
					organizerInfo.setEditCache("");
					organizerInfoRepository.save(organizerInfo);
				}else if("2".equals(organizerInfo.getEditStatus())){
					organizerInfo.setAuditStatus("4");
					organizerInfo.setEditStatus("");
					//缓存顺序：organizerName[0]-parentOrgId[1]-parentOrgName[2]-organizerType[3]-organizerTypeName[4]-organizerLevel[5]-organizerLevelName[6]-
					//areaCode[7]-areaName[8]-organizerCode[9]-organizerFunc[10]-principle[11]-principleTel[12]-manager[13]-managerTel[14]-address[15]-
					//email[16]-fax[17]-roleId[18]-roleName[19]-permissionId[20]-permissionName[21]
					List<String> cacheList = StringUtils.splitToList(organizerInfo.getEditCache(), delimiter, 22);
					organizerInfo.setOrganizerName(cacheList.get(0));
					organizerInfo.setParentOrgId(StringUtils.toLong(cacheList.get(1)));
					organizerInfo.setOrganizerType(StringUtils.toInteger(cacheList.get(3)));
					organizerInfo.setOrganizerLevel(cacheList.get(5));
					organizerInfo.setAreaCode(cacheList.get(7));
					organizerInfo.setAreaName(cacheList.get(8));
					organizerInfo.setOrganizerCode(cacheList.get(9));
					organizerInfo.setOrganizerFunc(cacheList.get(10));
					organizerInfo.setPrinciple(cacheList.get(11));
					organizerInfo.setPrincipleTel(cacheList.get(12));
					organizerInfo.setManager(cacheList.get(13));
					organizerInfo.setManagerTel(cacheList.get(14));
					organizerInfo.setAddress(cacheList.get(15));
					organizerInfo.setEmail(cacheList.get(16));
					organizerInfo.setFax(cacheList.get(17));
					
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("roleId", cacheList.get(18));
					params.put("permissionId", cacheList.get(20));
					organizerInfo.setEditCache("");
					editOrganizer(organizerInfo, userInfo, params);
				}else if("3".equals(organizerInfo.getEditStatus())){
					organizerInfo.setIsValid(Constants.IS_VALID_INVALID);
					organizerInfo.setAuditStatus("4");
					organizerInfo.setEditStatus("");
					organizerInfo.setEditCache("");
					organizerInfoRepository.save(organizerInfo);
				}else{
					organizerInfo.setAuditStatus("4");
					organizerInfo.setEditStatus("");
					organizerInfo.setEditCache("");
					organizerInfoRepository.save(organizerInfo);
				}
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
			organizerInfoRepository.auditNotPass(idList);
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public boolean isOrganizerManager(UserInfo userInfo) {
		//manager的格式userId + char(0) + userCode + char(0) + userName
		String manager = userInfo.getUserId() + Constants.SPLIT_CHAR_0 + userInfo.getUserCode() + Constants.SPLIT_CHAR_0 + userInfo.getUserName();
		List<OrganizerInfo> list = organizerInfoRepository.findByManagerAndIsValid(manager, Constants.IS_VALID_VALID);
		if(list != null && list.size() > 0) return true;
		else return false;
	}
	
	//分级获取（动态加载子节点）
//	public JSONArray getOrganizerTreeByParentId(Long parentOrgId, boolean filterDept, boolean filterPost) {
//		JSONArray treeJson = new JSONArray();
//		List<Map<String, Object>> orgList = organizerInfoRepository.getOrganizerTree(parentOrgId, null, filterDept, filterPost, null);
//		JSONObject node=new JSONObject();
//		JSONObject attr =new JSONObject();
//		for(Map<String, Object> map: orgList){
//			node = new JSONObject();
//			attr = new JSONObject();
//			node.put("id", MapUtils.getString(map, "organizerId"));
//			node.put("text", MapUtils.getString(map, "organizerName"));
//			if(MapUtils.getInteger(map, "childCount") > 0){
//				node.put("state", "{opened: false}");
//				//设置children状态，用于判断是否需要动态加载
//				node.put("children", true);
//			}else{
//				node.put("state", "{opened: true}");
//				//设置children状态，用于判断是否需要动态加载
//				node.put("children", false);
//			}
//			attr.put("parent", map.get("parentOrgId") == null ? "" : map.get("parentOrgId").toString());
//			attr.put("organizerType",MapUtils.getString(map, "organizerType"));
//			attr.put("organizerMemo", (map.get("organizerMemo") == null ? "" : map.get("organizerMemo").toString()));
//			attr.put("status", (map.get("status") == null ? "" : map.get("status").toString()));
//			attr.put("maxUsers", (map.get("maxUsers") == null ? "" : map.get("maxUsers").toString()));
//			attr.put("beginDate", (map.get("beginDate") == null ? "" : map.get("beginDate").toString()));
//			attr.put("endDate", (map.get("endDate") == null ? "" : map.get("endDate").toString()));
//			attr.put("idPath", (map.get("idPath") == null ? "" : map.get("idPath").toString()));
//			//设置图标
//			if(map.get("parentOrgId") == null){
//				node.put("icon", "fa fa-sitemap");
//			}else{
//				if("1".equals(MapUtils.getString(map, "organizerType"))){
//					node.put("icon", "fa fa-building");
//				}else if("2".equals(MapUtils.getString(map, "organizerType"))){
//					node.put("icon", "fa fa-cubes");
//				}else if("3".equals(MapUtils.getString(map, "organizerType"))){
//					node.put("icon", "fa fa-address-card-o");
//				}
//			}
//			node.put("attributes", attr);
//			treeJson.add(node);
//		}
//		return treeJson;
//	}
//
//	//分级获取（动态加载子节点）
//	public JSONArray getOrganizerTreeById(Long organizerId, UserInfo userInfo, Long parentOrgId, boolean filterDept, boolean filterPost) {
//		if(parentOrgId == null || parentOrgId.equals(-1L)) {
//			if(organizerId == null  || organizerId.equals(-1L)) 
//				organizerId = userInfo.getOrganizerId();
//			JSONArray treeJson = new JSONArray();
//			List<Map<String, Object>> orgList = organizerInfoRepository.getOrganizerTree(null, organizerId, filterDept, filterPost, null);
//			JSONObject node = new JSONObject();
//			JSONObject attr =new JSONObject();
//			for(Map<String, Object> map: orgList){
//				node = new JSONObject();
//				attr =new JSONObject();
//				node.put("id", MapUtils.getString(map, "organizerId"));
//				node.put("text", MapUtils.getString(map, "organizerName"));
//				if(MapUtils.getInteger(map, "childCount") > 0){
//					node.put("children", getOrganizerTreeByParentId(organizerId, filterDept, filterPost));
//				}
//				attr.put("parent", map.get("parentOrgId") == null ? "" : map.get("parentOrgId").toString());
//				attr.put("organizerType",MapUtils.getString(map, "organizerType"));
//				attr.put("organizerMemo", (map.get("organizerMemo") == null ? "" : map.get("organizerMemo").toString()));
//				attr.put("status", (map.get("status") == null ? "" : map.get("status").toString()));
//				attr.put("maxUsers", (map.get("maxUsers") == null ? "" : map.get("maxUsers").toString()));
//				attr.put("beginDate", (map.get("beginDate") == null ? "" : map.get("beginDate").toString()));
//				attr.put("endDate", (map.get("endDate") == null ? "" : map.get("endDate").toString()));
//				attr.put("idPath", (map.get("idPath") == null ? "" : map.get("idPath").toString()));
//				//设置图标
//				if(map.get("parentOrgId") == null){
//					node.put("icon", "fa fa-sitemap");
//				}else{
//					if("1".equals(MapUtils.getString(map, "organizerType"))){
//						node.put("icon", "fa fa-building");
//					}else if("2".equals(MapUtils.getString(map, "organizerType"))){
//						node.put("icon", "fa fa-cubes");
//					}else if("3".equals(MapUtils.getString(map, "organizerType"))){
//						node.put("icon", "fa fa-address-card-o");
//					}
//				}
//				node.put("attributes", attr);
//				treeJson.add(node);
//			}
//			return treeJson;
//		}
//		else 
//			return getOrganizerTreeByParentId(parentOrgId, filterDept, filterPost);
//	}
//
//	//分级获取（动态加载子节点）
//	public JSONArray getOrganizerTreeByTenantsId(Long tenantsId, UserInfo userInfo, Long parentOrgId, boolean filterDept, boolean filterPost) {
//		if(parentOrgId == null || parentOrgId.equals(-1L)) {
//			JSONArray treeJson = new JSONArray();
//			List<Map<String, Object>> orgList = organizerInfoRepository.getOrganizerTree(null, null, filterDept, filterPost, tenantsId);
//			JSONObject node=new JSONObject();
//			JSONObject attr =new JSONObject();
//			for(Map<String, Object> map: orgList){
//				node = new JSONObject();
//				attr =new JSONObject();
//				node.put("id", MapUtils.getString(map, "organizerId"));
//				node.put("text", MapUtils.getString(map, "organizerName"));
//				if(MapUtils.getInteger(map, "childCount") > 0){
//					node.put("children", getOrganizerTreeByParentId(MapUtils.getLong(map, "organizerId"), filterDept, filterPost));
//				}
//				attr.put("parent", map.get("parentOrgId") == null ? "" : map.get("parentOrgId").toString());
//				attr.put("organizerType",MapUtils.getString(map, "organizerType"));
//				attr.put("organizerMemo", (map.get("organizerMemo") == null ? "" : map.get("organizerMemo").toString()));
//				attr.put("status", (map.get("status") == null ? "" : map.get("status").toString()));
//				attr.put("maxUsers", (map.get("maxUsers") == null ? "" : map.get("maxUsers").toString()));
//				attr.put("beginDate", (map.get("beginDate") == null ? "" : map.get("beginDate").toString()));
//				attr.put("endDate", (map.get("endDate") == null ? "" : map.get("endDate").toString()));
//				attr.put("idPath", (map.get("idPath") == null ? "" : map.get("idPath").toString()));
//				//设置图标
//				if(map.get("parentOrgId") == null){
//					node.put("icon", "fa fa-sitemap");
//				}else{
//					if("1".equals(MapUtils.getString(map, "organizerType"))){
//						node.put("icon", "fa fa-building");
//					}else if("2".equals(MapUtils.getString(map, "organizerType"))){
//						node.put("icon", "fa fa-cubes");
//					}else if("3".equals(MapUtils.getString(map, "organizerType"))){
//						node.put("icon", "fa fa-address-card-o");
//					}
//				}
//				node.put("attributes", attr);
//				treeJson.add(node);
//			}
//			return treeJson;
//		}
//		else 
//			return getOrganizerTreeByParentId(parentOrgId, filterDept, filterPost);
//	}
}
