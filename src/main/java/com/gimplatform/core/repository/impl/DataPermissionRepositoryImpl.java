package com.gimplatform.core.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.springframework.transaction.annotation.Transactional;
import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.DataPermissionRepositoryCustom;
import com.gimplatform.core.utils.StringUtils;

@Transactional
public class DataPermissionRepositoryImpl extends BaseRepository implements DataPermissionRepositoryCustom {
	
	//获取用户列表
	private static final String MYSQL_SQL_GET_USER_LIST_BY_DATA_PERMISSION = "SELECT u.USER_ID as \"userId\", u.USER_NAME as \"userName\", u.USER_CODE as \"userCode\", u.IS_ADMIN as \"isAdmin\", "
					+ "o.name_path as \"namePath\", o.organizer_type as \"organizerType\" "
			+ "FROM sys_user_info u left join sys_organizer_info o on (u.organizer_id = o.organizer_id) "
			+ "WHERE u.IS_VALID = 'Y' ";
	
	//获取用户列表
	private static final String MYSQL_SQL_GET_USER_LIST_COUNT_DATA_PERMISSION = "SELECT count(1) as \"count\"  "
			+ "FROM sys_user_info u left join sys_organizer_info o on (u.organizer_id = o.organizer_id) "
			+ "WHERE u.IS_VALID = 'Y' ";
	
	private static final String MYSQL_SQL_GET_ALL_USER_LIST_BY_DATA_PERMISSION = "SELECT u.USER_ID as \"userId\", u.USER_NAME as \"userName\", u.USER_CODE as \"userCode\", u.IS_ADMIN as \"isAdmin\", "
					+ "u.organizer_id as \"organizerId\" "
			+ "FROM sys_user_info u left join sys_user_data_permission udp on udp.USER_ID = u.USER_ID "
			+ "WHERE u.IS_VALID = 'Y' AND udp.PERMISSION_ID in (select dpr.PERMISSION_ID from sys_data_permission_recur dpr WHERE dpr.PERMISSION_CHILD_ID = :dataPermissionId)";
	
	public DataPermissionRepositoryImpl(){
		sqlMap = new HashMap<String, String>();
		sqlMap.put("MYSQL_SQL_GET_USER_LIST_BY_DATA_PERMISSION", MYSQL_SQL_GET_USER_LIST_BY_DATA_PERMISSION);
		sqlMap.put("MYSQL_SQL_GET_USER_LIST_COUNT_DATA_PERMISSION", MYSQL_SQL_GET_USER_LIST_COUNT_DATA_PERMISSION);
		sqlMap.put("MYSQL_SQL_GET_ALL_USER_LIST_BY_DATA_PERMISSION", MYSQL_SQL_GET_ALL_USER_LIST_BY_DATA_PERMISSION);
		sqlMap.put("ORACLE_SQL_GET_USER_LIST_BY_DATA_PERMISSION", MYSQL_SQL_GET_USER_LIST_BY_DATA_PERMISSION);
		sqlMap.put("ORACLE_SQL_GET_USER_LIST_COUNT_DATA_PERMISSION", MYSQL_SQL_GET_USER_LIST_COUNT_DATA_PERMISSION);
		sqlMap.put("ORACLE_SQL_GET_ALL_USER_LIST_BY_DATA_PERMISSION", MYSQL_SQL_GET_ALL_USER_LIST_BY_DATA_PERMISSION);
	}
	
	public List<Map<String, Object>> getUserListByDataPermission(Map<String, Object> params, int pageIndex, int pageSize) {
		//生成查询条件
		SqlParams sqlParams = genUserListByDataPermissionWhere(getSqlContent("SQL_GET_USER_LIST_BY_DATA_PERMISSION"), params);
		//添加分页和排序
		sqlParams = getPageableSql(sqlParams, pageIndex, pageSize);
		//sqlParams = getPageableSql(sqlParams, pageIndex, pageSize, " USER_ID DESC ", " \"userId\" DESC ");
		return getResultList(sqlParams);
	}

	public int getUserListCountByDataPermission(Map<String, Object> params) {
		//生成查询条件
		SqlParams sqlParams = genUserListByDataPermissionWhere(getSqlContent("SQL_GET_USER_LIST_COUNT_DATA_PERMISSION"), params);
		return getResultListTotalCount(sqlParams);
	}
	
	/**
	 * 生成查询条件
	 * @param sql
	 * @param params
	 * @return
	 */
	private SqlParams genUserListByDataPermissionWhere(String sql, Map<String, Object> params){
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(sql);
		String tenantsId = MapUtils.getString(params, "tenantsId");
		String organizerId = MapUtils.getString(params, "organizerId");
		String permissionId = MapUtils.getString(params, "permissionId");
		String dataType = MapUtils.getString(params, "dataType");
		String userType = MapUtils.getString(params, "userType");
		String userName = MapUtils.getString(params, "userName");
		//添加查询参数
		if(!StringUtils.isBlank(userName)) {
		    sqlParams.querySql.append(getLikeSql("u.user_name", ":userName"));
			//sqlParams.querySql.append(" AND u.user_name like concat('%', :userName,'%') ");
			sqlParams.paramsList.add("userName");
			sqlParams.valueList.add(userName);
        }
		if(!StringUtils.isBlank(permissionId) && "in".equals(dataType)) {
			sqlParams.querySql.append(" AND u.USER_ID IN (select distinct(USER_ID) from sys_user_data_permission where PERMISSION_ID = :permissionId) ");
			sqlParams.paramsList.add("permissionId");
			sqlParams.valueList.add(permissionId);
        }
		if(!StringUtils.isBlank(permissionId) && "notIn".equals(dataType)) {
			sqlParams.querySql.append(" AND u.USER_ID not in(select distinct(USER_ID) from sys_user_data_permission where PERMISSION_ID = :permissionId) ");
			sqlParams.paramsList.add("permissionId");
			sqlParams.valueList.add(permissionId);
        }
        if(!StringUtils.isBlank(tenantsId)) {
        	sqlParams.querySql.append(" AND u.TENANTS_ID = :tenantsId ");
        	sqlParams.paramsList.add("tenantsId");
        	sqlParams.valueList.add(tenantsId);
        }
        if(!StringUtils.isBlank(organizerId)) {
        	//查找当前组织下的所有子组织
        	sqlParams.querySql.append(" AND u.organizer_id in(select distinct(ORGANIZER_CHILD_ID) from sys_organizer_recur where organizer_id=:organizerId ) ");
			//querySql.append(" AND u.ORGANIZER_ID=:organizerId ");
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

	public List<Map<String, Object>> getAllUserListByDataPermission(Long dataPermissionId) {
		//组合查询条件
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(getSqlContent("SQL_GET_ALL_USER_LIST_BY_DATA_PERMISSION"));
		sqlParams.paramsList.add("dataPermissionId");
		sqlParams.valueList.add(dataPermissionId);
		return getResultList(sqlParams);
	}
}
