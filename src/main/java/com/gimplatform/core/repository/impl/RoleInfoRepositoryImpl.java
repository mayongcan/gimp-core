package com.gimplatform.core.repository.impl;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.springframework.transaction.annotation.Transactional;
import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.RoleInfoRepositoryCustom;
import com.gimplatform.core.utils.StringUtils;

@Transactional
public class RoleInfoRepositoryImpl extends BaseRepository implements RoleInfoRepositoryCustom {
	
	//获取角色用户列表
	private static final String SQL_GET_ROLE_USER_LIST = "SELECT u.USER_ID as \"userId\", u.USER_NAME as \"userName\", u.USER_CODE as \"userCode\", "
					+ "u.IS_ADMIN as \"isAdmin\", o.name_path as \"namePath\", o.organizer_type as \"organizerType\" "
			+ "FROM sys_user_info u left join sys_organizer_info o on (u.organizer_id = o.organizer_id) "
			+ "where u.IS_VALID = 'Y' ";
	
	//获取角色用户列表
	private static final String SQL_GET_ROLE_USER_LIST_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM sys_user_info u left join sys_organizer_info o on (u.organizer_id = o.organizer_id) "
			+ "where u.IS_VALID = 'Y' ";

	public List<Map<String, Object>> getRoleUserList(Map<String, Object> params, int pageIndex, int pageSize) {
		//生成查询条件
		SqlParams sqlParams = genRoleUserListWhere(SQL_GET_ROLE_USER_LIST, params);
		//添加分页和排序
		sqlParams = getPageableSql(sqlParams, pageIndex, pageSize);
		return getResultList(sqlParams);
	}

	public int getRoleUserListCount(Map<String, Object> params) {
		//生成查询条件
		SqlParams sqlParams = genRoleUserListWhere(SQL_GET_ROLE_USER_LIST_COUNT, params);
		return getResultListTotalCount(sqlParams);
	}
	
	/**
	 * 生成查询条件
	 * @param sql
	 * @param params
	 * @return
	 */
	private SqlParams genRoleUserListWhere(String sql, Map<String, Object> params){
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(sql);
		String roleId = MapUtils.getString(params, "roleId", null);
		String organizerId = MapUtils.getString(params, "organizerId");
		String tenantsId = MapUtils.getString(params, "tenantsId");
		String dataType = MapUtils.getString(params, "dataType");
		String userType = MapUtils.getString(params, "userType");
		String userName = MapUtils.getString(params, "userName");
		//添加查询参数
		if(!StringUtils.isBlank(userName)) {
			sqlParams.querySql.append(" AND u.user_name like concat('%', :userName,'%') ");
			sqlParams.paramsList.add("userName");
			sqlParams.valueList.add(userName);
        }
		if(roleId != null && "in".equals(dataType)) {
			sqlParams.querySql.append(" AND u.USER_ID IN (select distinct(USER_ID) from sys_user_role where ROLE_ID=:roleId) ");
			sqlParams.paramsList.add("roleId");
			sqlParams.valueList.add(roleId);
        }
		if(roleId != null && "notIn".equals(dataType)) {
			sqlParams.querySql.append(" AND u.USER_ID not in(select distinct(USER_ID) from sys_user_role where ROLE_ID=:roleId) ");
			sqlParams.paramsList.add("roleId");
			sqlParams.valueList.add(roleId);
        }
        if(tenantsId != null) {
        	sqlParams.querySql.append(" AND u.TENANTS_ID=:tenantsId ");
        	sqlParams.paramsList.add("tenantsId");
        	sqlParams.valueList.add(tenantsId);
        }
        if(organizerId != null) {
        	//查找当前组织下的所有子组织
        	sqlParams.querySql.append(" AND u.organizer_id in(select distinct(ORGANIZER_CHILD_ID) from sys_organizer_recur where organizer_id=:organizerId ) ");
			//sqlParams.querySql.append(" AND u.ORGANIZER_ID=:organizerId ");
        	sqlParams.paramsList.add("organizerId");
        	sqlParams.valueList.add(organizerId);
        }
        if(!StringUtils.isBlank(userType)){
        	sqlParams.querySql.append(" AND u.USER_TYPE=:userType ");
        	sqlParams.paramsList.add("userType");
        	sqlParams.valueList.add(userType);
        }
        return sqlParams;
	}

	@Override
	public List<Map<String, Object>> getRolesKeyValByOrganizerId(Long organizerId) {
		//组合查询条件
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append("SELECT r.ROLE_ID as ID, r.ROLE_NAME as NAME FROM sys_role_info r WHERE IS_VALID = 'Y' ");
        if(organizerId != null) {
        	sqlParams.querySql.append(" AND r.organizer_id = :organizerId ");
        	sqlParams.paramsList.add("organizerId");
        	sqlParams.valueList.add(organizerId);
        }
    	sqlParams.querySql.append(" ORDER BY ROLE_ID ASC ");
		return getResultList(sqlParams);
	}

}
