package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.DataPermission;
import com.gimplatform.core.entity.DataPermissionRecur;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.repository.DataPermissionRepository;
import com.gimplatform.core.service.DataPermissionService;
import com.gimplatform.core.tree.Tree;
import com.gimplatform.core.tree.TreeNode;
import com.gimplatform.core.tree.TreeNodeExtend;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 数据权限信息服务类
 * @author zzd
 *
 */
@Service
public class DataPermissionServiceImpl implements DataPermissionService{

    @Autowired
    private DataPermissionRepository dataPermissionRepository;

	@Override
	public JSONObject getUserListByDataPermission(Pageable page, Map<String, Object> params) {
		List<Map<String, Object>> list = dataPermissionRepository.getUserListByDataPermission(params, page.getPageNumber(), page.getPageSize());
		int count = dataPermissionRepository.getUserListCountByDataPermission(params);
		return RestfulRetUtils.getRetSuccessWithPage(list, count);	
	}
    
	@Override
	public JSONObject getTreeList(Long tenantsId, Long organizerId){
		List<DataPermission> list = new ArrayList<DataPermission>();
		if(organizerId != null)
			list = dataPermissionRepository.getTreeList(tenantsId, organizerId);
		else 
			list = dataPermissionRepository.getTreeListByTenantsId(tenantsId);
		return getJsonTree(list);
	}
    
	@Override
	public JSONObject getRootTreeList(Long tenantsId, Long organizerId){
		List<DataPermission> list = new ArrayList<DataPermission>();
		if(organizerId != null)
			list = dataPermissionRepository.getListByRootAndTenantsIdAndOrganizerId(tenantsId, organizerId);
		else 
			list = dataPermissionRepository.getListByRootAndTenantsId(tenantsId);
		return getJsonTree(list);
	}

	@Override
	public List<DataPermission> getListByParentIds(DataPermission dataPermission){
		List<Long> idList = new ArrayList<>();
		Long parentId = (long) 0;
		if (dataPermission != null) {
			parentId = dataPermission.getParentId();

		}
		List<DataPermission> treeList = new ArrayList<>();
		if (parentId == 0) {
			treeList = dataPermissionRepository.getListByRoot();
		} else {
			idList.add(parentId);
			treeList = dataPermissionRepository.getListByParentIds(idList);
		}
		return treeList;
	}

	@Override
	public JSONObject addDataPermission(DataPermission dataPermission, UserInfo userInfo) {
		if(dataPermission.getTenantsId() == null) dataPermission.setTenantsId(userInfo.getTenantsId());
		if(dataPermission.getOrganizerId() == null) dataPermission.setOrganizerId(userInfo.getOrganizerId());
		dataPermission.setIsValid(Constants.IS_VALID_VALID);
		if(dataPermissionRepository.findByPermissionNameAndTenantsIdAndOrganizerIdAndIsValid(dataPermission.getPermissionName(),
				dataPermission.getTenantsId(), dataPermission.getOrganizerId(), Constants.IS_VALID_VALID).size() > 0){
			return RestfulRetUtils.getErrorMsg("25006","输入名称已存在");
		}
		dataPermission.setCreateDate(new Date());
		dataPermission.setCreateBy(userInfo.getUserId());
		dataPermission.setModifyBy(userInfo.getUserId());
		dataPermission.setModifyDate(new Date());
		dataPermission = dataPermissionRepository.save(dataPermission);
		dataPermissionRepository.flush();
		
		//刷新递归子表
		addDataPermissionRecur(dataPermission);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject editDataPermission(DataPermission dataPermission, UserInfo userInfo) {
		if(dataPermission.getTenantsId() == null) dataPermission.setTenantsId(userInfo.getTenantsId());
		if(dataPermission.getOrganizerId() == null) dataPermission.setOrganizerId(userInfo.getOrganizerId());
		dataPermission.setIsValid(Constants.IS_VALID_VALID);
		DataPermission dataPermissionInDb = dataPermissionRepository.findOne(dataPermission.getPermissionId());
		if(dataPermissionInDb == null){
			return RestfulRetUtils.getErrorMsg("25007","当前编辑的数据不存在");
		}
		if(dataPermissionInDb != null && !dataPermissionInDb.getPermissionName().equals(dataPermission.getPermissionName()) && 
				dataPermissionRepository.findByPermissionNameAndTenantsIdAndOrganizerIdAndIsValid(dataPermission.getPermissionName(),
						dataPermission.getTenantsId(), dataPermission.getOrganizerId(), Constants.IS_VALID_VALID).size() > 0){
			return RestfulRetUtils.getErrorMsg("25006","输入名称已存在");
		}
		//合并两个javabean
		BeanUtils.mergeBean(dataPermission, dataPermissionInDb);

		dataPermissionInDb.setModifyBy(userInfo.getUserId());
		dataPermissionInDb.setModifyDate(new Date());
		dataPermissionRepository.save(dataPermissionInDb);
		
		//刷新递归子表
		addDataPermissionRecur(dataPermission);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject delDataPermission(Long permissionId, UserInfo userInfo) {
		DataPermission dataPermission = dataPermissionRepository.findOne(permissionId);
		if(dataPermission != null){
			if(Constants.IS_VALID_VALID.equals(dataPermission.getIsFix())){
				return RestfulRetUtils.getErrorMsg("25016","不能删除固定的值");
			}
			dataPermission.setIsValid(Constants.IS_VALID_INVALID);
			dataPermission.setModifyBy(userInfo.getUserId());
			dataPermission.setModifyDate(new Date());
			dataPermissionRepository.save(dataPermission);
			//删除对应用户角色数据
			dataPermissionRepository.delUserDataPermissionByPermissionId(dataPermission.getPermissionId());
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject addUserDataPermission(UserInfo userInfo, Long permissionId, List<Long> userIdList) {
		for(Long userId : userIdList){
			dataPermissionRepository.saveUserDataPermission(userId, permissionId);
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject delUserDataPermission(UserInfo userInfo, Long permissionId, List<Long> userIdList) {
		dataPermissionRepository.delUserDataPermissionByPermissionIdAndUserId(permissionId, userIdList);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public List<DataPermission> getTreeListByParentId(Long parentId) {
		List<Long> idList = new ArrayList<Long>();
		idList.add(parentId);
		List<DataPermission> dataPermissionList = null; 
		int totalCount = 0;
		while(true){
			dataPermissionList = dataPermissionRepository.getListByParentIds(idList);
			if(dataPermissionList != null) {
				//判断最新的数组列表是否和上一次获取到的一样，如果一样，则已获取所有数据 
				if(dataPermissionList.size() == totalCount){
					break;
				}else{
					totalCount = dataPermissionList.size();
					for(DataPermission obj : dataPermissionList){
						idList.add(obj.getPermissionId());
					}
					//去除重复值
					HashSet<Long> h = new HashSet<Long>(idList); 
					idList.clear(); 
					idList.addAll(h); 
				}
			}else{
				break;
			}
		}
		return dataPermissionList;
	}

	@Override
	public Map<String, String> getTreeListByUser(UserInfo userInfo) {
		List<DataPermission> list1 = dataPermissionRepository.getListByUserCode(userInfo.getUserCode());
		List<DataPermission> list2 = dataPermissionRepository.getListByOrganizerId(userInfo.getOrganizerId());
		//去重
		List<DataPermission> compareList = new ArrayList<DataPermission>();
		compareList.addAll(list1);
		compareList.addAll(list2);
		Set<DataPermission> set = new TreeSet<DataPermission>(new Comparator<DataPermission>(){
	           @Override
	           public int compare(DataPermission o1, DataPermission o2) {
	                return o1.getPermissionId().compareTo(o2.getPermissionId());
	           }
	      });
		set.addAll(compareList);
		List<DataPermission> retList = new ArrayList<DataPermission>(set);
		
		String idList = "", nameList = "", firstId = "", firstName = "" ;
		for(DataPermission obj : retList){
			if(StringUtils.isBlank(firstId)) firstId = obj.getPermissionId() + "";
			if(StringUtils.isBlank(firstName)) firstName = obj.getPermissionName() + "";
			
			idList += obj.getPermissionId() + ",";
			nameList += obj.getPermissionName() + ",";
			List<DataPermission> tmpList = getTreeListByParentId(obj.getPermissionId());
			for(DataPermission tmpObj : tmpList){
				idList += tmpObj.getPermissionId() + ",";
				nameList += tmpObj.getPermissionName() + ",";
			}
		}
		if(!StringUtils.isBlank(idList)) idList = idList.substring(0, idList.length() - 1);
		if(!StringUtils.isBlank(nameList)) nameList = nameList.substring(0, nameList.length() - 1);
		Map<String, String> retMap = new HashMap<String, String>();
		retMap.put("idList", idList);
		retMap.put("nameList", nameList);
		//取第一个选中的内容
		retMap.put("firstId", firstId);
		retMap.put("firstName", firstName);
		return retMap;
	}
	
	/**
	 * 获取json格式的树
	 * @param list
	 * @return
	 */
	private JSONObject getJsonTree(List<DataPermission> list) {
		TreeNode root = new TreeNode("root", "all", null, false);
		Map<String, String> mapAttr = null;
		TreeNodeExtend treeNode = null;
		String id = "", text = "", parent = "";
		Tree tree = new Tree(true);
		// 添加一个自定义的根节点
		if (list == null || list.isEmpty()) {
			treeNode = new TreeNodeExtend("-1", "虚拟节点", "", false, null);
			tree.addNode(treeNode);
		}else{
			for (DataPermission obj : list) {
				if (obj == null || obj.getPermissionId() == null)
					continue;
				id = obj.getPermissionId().toString();
				text = obj.getPermissionName();
				parent = obj.getParentId() == null ? "" : obj.getParentId().toString();

				mapAttr = new HashMap<String, String>();
				mapAttr = BeanUtils.beanToMapStr(obj);

				treeNode = new TreeNodeExtend(id, text, parent, false, mapAttr);
				tree.addNode(treeNode);
			}
		}
		String strTree = tree.getTreeJson(tree, root);
		return RestfulRetUtils.getRetSuccess(JSONArray.parseArray(strTree));
	}
	
	/**
	 * 写入递归子表
	 * @param dataPermission
	 */
	private void addDataPermissionRecur(DataPermission dataPermission) {
		dataPermissionRepository.delDataPermissionRecurByChildId(dataPermission.getPermissionId());
		//保存当前节点信息
		DataPermissionRecur dataPermissionRecur = null;
		dataPermissionRepository.saveDataPermissionRecur(dataPermission.getPermissionId(), dataPermission.getPermissionId());
		
		Long parentId = dataPermission.getParentId();
		if(parentId != null){
			int deep=0;
			DataPermission parentObj = null;
			while(parentId != null && deep< Constants.DEFAULT_TREE_DEEP){
				parentObj = dataPermissionRepository.findOne(parentId);
				if(parentObj != null){
					dataPermissionRecur = new DataPermissionRecur();
					dataPermissionRecur.setPermissionId(parentObj.getPermissionId());
					dataPermissionRecur.setPermissionChildId(dataPermission.getPermissionId());
					parentId = parentObj.getParentId();
					dataPermissionRepository.saveDataPermissionRecur(dataPermissionRecur.getPermissionId(), dataPermissionRecur.getPermissionChildId());
				}else{
					parentId = null;
				}
				deep++;
			}
		}
	}
}
