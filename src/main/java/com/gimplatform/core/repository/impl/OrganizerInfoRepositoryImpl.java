package com.gimplatform.core.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;

import org.apache.commons.collections4.MapUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.OrganizerInfoRepositoryCustom;
import com.gimplatform.core.utils.SessionUtils;

@Transactional
public class OrganizerInfoRepositoryImpl extends BaseRepository implements OrganizerInfoRepositoryCustom{
	
	private static final String SQL_GET_ORGANIZER_TREE = "SELECT org.ORGANIZER_ID as \"organizerId\", org.ORGANIZER_NAME as \"organizerName\", org.PARENT_ORG_ID as \"parentOrgId\", "
					+ "org.ORGANIZER_TYPE as \"organizerType\", org.ORGANIZER_MEMO as \"organizerMemo\", org.ADDRESS as \"address\", org.ORGANIZER_CODE as \"organizerCode\", "
					+ "org.AREA_CODE as \"areaCode\", org.AREA_NAME as \"areaName\", org.ORGANIZER_LEVEL as \"organizerLevel\", org.ORGANIZER_FUNC as \"organizerFunc\", "
					+ "org.PRINCIPLE as \"principle\", org.PRINCIPLE_TEL as \"principleTel\", org.MANAGER as \"manager\", org.MANAGER_TEL as \"managerTel\", "
					+ "org.EMAIL as \"email\", org.FAX as \"fax\", org.STATUS as \"status\", org.MAX_USERS as \"maxUsers\", org.BEGIN_DATE as \"beginDate\", org.END_DATE as \"endDate\", "
					+ "org.ID_PATH as \"idPath\", org.NAME_PATH as \"namePath\", org.AUDIT_STATUS as \"auditStatus\", org.EDIT_STATUS as \"editStatus\", org.EDIT_CACHE as \"editCache\", "
					+ "(SELECT COUNT(1) FROM SYS_ORGANIZER_INFO WHERE IS_VALID = 'Y' AND PARENT_ORG_ID = org.ORGANIZER_ID) AS \"childCount\" "
			+ "FROM SYS_ORGANIZER_INFO org left join sys_organizer_data_permission odp on odp.ORGANIZER_ID = org.ORGANIZER_ID "
			+ "WHERE org.IS_VALID = 'Y' AND (org.EDIT_STATUS IS NULL OR org.EDIT_STATUS != '1') ";  //修改状态不为新增，则显示树列表
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getOrganizerTree(Map<String, Object> params) {
		StringBuffer querySql = new StringBuffer(SQL_GET_ORGANIZER_TREE);
		Long parentOrgId = MapUtils.getLong(params, "parentOrgId");
		Long organizerId = MapUtils.getLong(params, "organizerId");
		Long organizerType = MapUtils.getLong(params, "organizerType");
		boolean filterDept = MapUtils.getBooleanValue(params, "filterDept", false);
		boolean filterPost = MapUtils.getBooleanValue(params, "filterPost", false);
		boolean filterSub = MapUtils.getBooleanValue(params, "filterSub", false);
		//过滤数据权限
		boolean filterDataFunc = MapUtils.getBooleanValue(params, "filterDataFunc", false);
		Long tenantsId = MapUtils.getLong(params, "tenantsId");
		//添加查询参数
		List<String> paramsList = new ArrayList<String>();
		List<Object> valueList = new ArrayList<Object>();
		if(null != organizerId && !organizerId.equals(-1L)) {
			if(filterSub)
				querySql.append(" AND org.ORGANIZER_ID in(select distinct(ORGANIZER_CHILD_ID) from sys_organizer_recur where organizer_id = :organizerId ) ");
			else
				querySql.append(" AND org.ORGANIZER_ID = :organizerId ");
			paramsList.add("organizerId");
			valueList.add(organizerId);
        }
		if(filterDataFunc){
			//根据登录用户所具有的数据权限，和组织的默认权限进行相关过滤
			List<Long> dataPermissionIdList = SessionUtils.getUserDataPermission();
			if(dataPermissionIdList.size() > 0){
				querySql.append(" AND (odp.PERMISSION_ID is null OR odp.PERMISSION_ID IN (:idList) ) ");
				paramsList.add("idList");
				valueList.add(dataPermissionIdList);
			}
		}
        if(null != parentOrgId && !parentOrgId.equals(-1L)) {
			querySql.append(" AND org.PARENT_ORG_ID =:parentOrgId ");
			paramsList.add("parentOrgId");
			valueList.add(parentOrgId);
        }
        if(null != tenantsId && !tenantsId.equals(-1L)) {
        	//querySql.append(" AND org.TENANTS_ID =:tenantsId AND org.PARENT_ORG_ID IS NULL ");
			querySql.append(" AND org.TENANTS_ID =:tenantsId ");
			paramsList.add("tenantsId");
			valueList.add(tenantsId);
        }
        if(organizerType != null) {
			querySql.append(" AND org.ORGANIZER_TYPE = :organizerType ");
			paramsList.add("organizerType");
			valueList.add(organizerType);
        }
        if(filterDept) {
			querySql.append(" AND org.ORGANIZER_TYPE != 2 ");
        }
        if(filterPost) {
			querySql.append(" AND org.ORGANIZER_TYPE != 3 ");
        }
		querySql.append(" ORDER BY org.ORGANIZER_TYPE, org.ORGANIZER_ID ");
		
		//创建查询对象
		Query query = entityManager.createNativeQuery(querySql.toString()); 
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		//设置查询参数
		for(int i = 0; i < paramsList.size(); i++){
			query.setParameter(paramsList.get(i), valueList.get(i));
		}
		return query.getResultList();
	}
}
