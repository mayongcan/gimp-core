package com.gimplatform.core.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.transaction.annotation.Transactional;
import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.UserInfoRepositoryCustom;
import com.gimplatform.core.utils.StringUtils;

/**
 * 用户信息DAO实现类
 * @author zzd
 *
 */
@Transactional
public class UserInfoRepositoryImpl extends BaseRepository implements UserInfoRepositoryCustom{
	
	//用户列表sql
	private static final String MYSQL_SQL_USER_LIST = "SELECT a.user_name as \"userName\", a.email as \"email\", a.user_id as \"userId\", a.is_valid as \"isValid\", a.user_code as \"userCode\", "
	                + "a.sex as \"sex\", DATE_FORMAT(a.birthday,'%Y-%m-%d') as \"birthday\", a.organizer_id as \"organizerId\", a.USER_TYPE as \"userType\", a.STATUS as \"status\", a.REAL_NAME as \"realName\", "
	                + "a.tenants_id as \"tenantsId\", a.mobile as \"mobile\", a.phone as \"phone\", a.dept_id as \"deptId\", a.is_admin as \"isAdmin\", a.CREDENTIALS_TYPE as \"credentialsType\", "
	                + "a.CREDENTIALS_NUM as \"credentialsNum\", DATE_FORMAT(a.create_date,'%Y-%m-%d') as \"createDate\", a.ADDRESS as \"address\", a.PAY_PASSWORD as \"payPassword\", a.SAFETY_PASSWORD as \"safetyPassword\", "
                    + "b.organizer_name as \"organizerName\", b.name_path as \"namePath\", b.organizer_type as \"organizerType\", "
                    + "c.tenants_name as \"tenantsName\", "
                    + "DATE_FORMAT(d.valid_begin_date,'%Y-%m-%d') as \"beginDate\", DATE_FORMAT(d.valid_end_date,'%Y-%m-%d') as \"endDate\", d.lock_begin_date as \"lockBeginDate\", d.lock_end_date as \"lockEndDate\", "
					+ "d.lock_reason as \"lockReason\", DATE_FORMAT(d.last_logon_date,'%Y-%m-%d') as \"lastLogonDate\", d.last_logon_ip as \"lastLogonIP\", d.access_ipaddress as \"ipAddress\", d.online_status as \"onLineStatus\", "
					+ "d.DEVICE_TYPE as \"deviceType\", d.DEVICE_DETAIL as \"deviceDetail\", d.OS_DETAIL as \"osDetail\", d.NETWORK_INFO as \"networkInfo\", d.LNGLAT as \"lnglat\", d.LNGLAT_ADDR as \"lnglatAddr\", "
					+ "f.USER_CODE as \"superiorUserCode\", "
					+ "(SELECT group_concat(sysrole.ROLE_NAME) FROM sys_role_info sysrole left join sys_user_role urole on urole.role_id = sysrole.role_id left join sys_user_info sysuser on urole.user_id = sysuser.user_id WHERE sysuser.user_id = a.user_id) as \"userRole\" "
			+ "FROM sys_user_info a left join sys_organizer_info b on (a.organizer_id = b.organizer_id) "
					+ "left join sys_tenants_info c on (a.tenants_id = c.tenants_id) "
					+ "left join sys_user_logon d on (a.user_id = d.user_id) "
					+ "left join sys_organizer_post e on (a.user_id = e.SUBORDINATE_USER_ID ) "
					+ "left join sys_user_info f on (e.SUPERIOR_USER_ID = f.user_id ) "
			+ "WHERE a.is_valid = 'Y' ";
	private static final String MYSQL_SQL_USER_LIST_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM sys_user_info a left join sys_organizer_info b on (a.organizer_id = b.organizer_id) "
					+ "left join sys_tenants_info c on (a.tenants_id = c.tenants_id) "
					+ "left join sys_user_logon d on (a.user_id = d.user_id) "
					+ "left join sys_organizer_post e on (a.user_id = e.SUBORDINATE_USER_ID ) "
					+ "left join sys_user_info f on (e.SUPERIOR_USER_ID = f.user_id ) "
			+ "WHERE a.is_valid = 'Y' ";
	
	//用户列表sql
	private static final String ORACLE_SQL_USER_LIST = "SELECT a.user_name as \"userName\", a.email as \"email\", a.user_id as \"userId\", a.is_valid as \"isValid\", a.user_code as \"userCode\", "
					+ "a.sex as \"sex\", TO_CHAR(a.birthday,'YYYY-MM-DD') as \"birthday\", a.organizer_id as \"organizerId\", a.USER_TYPE as \"userType\", a.STATUS as \"status\", a.REAL_NAME as \"realName\", "
					+ "a.tenants_id as \"tenantsId\", a.mobile as \"mobile\", a.phone as \"phone\", a.dept_id as \"deptId\", a.is_admin as \"isAdmin\", a.CREDENTIALS_TYPE as \"credentialsType\", "
					+ "a.CREDENTIALS_NUM as \"credentialsNum\", TO_CHAR(a.create_date,'%Y-%m-%d') as \"createDate\", a.ADDRESS as \"address\", a.PAY_PASSWORD as \"payPassword\", a.SAFETY_PASSWORD as \"safetyPassword\", "
					+ "b.organizer_name as \"organizerName\", b.name_path as \"namePath\", b.organizer_type as \"organizerType\", "
					+ "c.tenants_name as \"tenantsName\", "
					+ "TO_CHAR(d.valid_begin_date,'YYYY-MM-DD') as \"beginDate\", TO_CHAR(d.valid_end_date,'YYYY-MM-DD') as \"endDate\", d.lock_begin_date as \"lockBeginDate\", d.lock_end_date as \"lockEndDate\", "
					+ "d.lock_reason as \"lockReason\", TO_CHAR(d.last_logon_date,'YYYY-MM-DD') as \"lastLogonDate\", d.last_logon_ip as \"lastLogonIP\", d.access_ipaddress as \"ipAddress\", d.online_status as \"onLineStatus\", "
					+ "d.DEVICE_TYPE as \"deviceType\", d.DEVICE_DETAIL as \"deviceDetail\", d.OS_DETAIL as \"osDetail\", d.NETWORK_INFO as \"networkInfo\", d.LNGLAT as \"lnglat\", d.LNGLAT_ADDR as \"lnglatAddr\", "
					+ "f.USER_CODE as \"superiorUserCode\", "
					+ "(SELECT wm_concat(sysrole.ROLE_NAME) FROM sys_role_info sysrole left join sys_user_role urole on urole.role_id = sysrole.role_id left join sys_user_info sysuser on urole.user_id = sysuser.user_id WHERE sysuser.user_id = a.user_id) as \"userRole\" "
			+ "FROM sys_user_info a left join sys_organizer_info b on (a.organizer_id = b.organizer_id) "
					+ "left join sys_tenants_info c on (a.tenants_id = c.tenants_id) "
					+ "left join sys_user_logon d on (a.user_id = d.user_id) "
					+ "left join sys_organizer_post e on (a.user_id = e.SUBORDINATE_USER_ID ) "
					+ "left join sys_user_info f on (e.SUPERIOR_USER_ID = f.user_id ) "
			+ "WHERE a.is_valid = 'Y' ";
	private static final String ORACLE_SQL_USER_LIST_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM sys_user_info a left join sys_organizer_info b on (a.organizer_id = b.organizer_id) "
					+ "left join sys_tenants_info c on (a.tenants_id = c.tenants_id) "
					+ "left join sys_user_logon d on (a.user_id = d.user_id) "
					+ "left join sys_organizer_post e on (a.user_id = e.SUBORDINATE_USER_ID ) "
					+ "left join sys_user_info f on (e.SUPERIOR_USER_ID = f.user_id ) "
			+ "WHERE a.is_valid = 'Y' ";

	public UserInfoRepositoryImpl(){
		sqlMap = new HashMap<String, String>();
		sqlMap.put("MYSQL_SQL_USER_LIST", MYSQL_SQL_USER_LIST);
		sqlMap.put("MYSQL_SQL_USER_LIST_COUNT", MYSQL_SQL_USER_LIST_COUNT);
		sqlMap.put("ORACLE_SQL_USER_LIST", ORACLE_SQL_USER_LIST);
		sqlMap.put("ORACLE_SQL_USER_LIST_COUNT", ORACLE_SQL_USER_LIST_COUNT);
	}

	public List<Map<String, Object>> getUserList(UserInfo userInfo, Map<String, Object> params, int pageIndex, int pageSize) {
		//生成查询条件
		SqlParams sqlParams = genUserListWhere(getSqlContent("SQL_USER_LIST"), userInfo, params);
		//添加分页和排序
		sqlParams = getPageableSql(sqlParams, pageIndex, pageSize, " a.user_id DESC ", " \"userId\" DESC ");
		return getResultList(sqlParams);
	}

	public int getUserListCount(UserInfo userInfo, Map<String, Object> params) {
		//生成查询条件
		SqlParams sqlParams = genUserListWhere(getSqlContent("SQL_USER_LIST_COUNT"), userInfo, params);
		return getResultListTotalCount(sqlParams);
	}
	
	/**
	 * 生成查询条件
	 * @param sql
	 * @param userInfo
	 * @param organizerName
	 * @param findChildUsers
	 * @param roleId
	 * @return
	 */
	private SqlParams genUserListWhere(String sql, UserInfo userInfo, Map<String, Object> params){
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(sql);
		String organizerName = MapUtils.getString(params, "organizerName");
		int findChildUsers = MapUtils.getIntValue(params, "findChildUsers", 1);
//		Long roleId = MapUtils.getLong(params, "roleId", null);
		String roleId = MapUtils.getString(params, "roleId", "");
		String notInAccount = MapUtils.getString(params, "notInAccount");
		//添加查询参数
		if(null != userInfo && !StringUtils.isBlank(userInfo.getUserName())) {
            sqlParams.querySql.append(getLikeSql("a.user_name", ":userName"));
			sqlParams.paramsList.add("userName");
			sqlParams.valueList.add(userInfo.getUserName());
        }
		if(null != userInfo && !StringUtils.isBlank(userInfo.getUserCode())) {
            sqlParams.querySql.append(getLikeSql("a.user_code", ":userCode"));
			sqlParams.paramsList.add("userCode");
			sqlParams.valueList.add(userInfo.getUserCode());
        }
        if(!StringUtils.isBlank(organizerName)) {
        	sqlParams.querySql.append(" AND b.organizer_name = :organizerName ");
        	sqlParams.paramsList.add("organizerName");
        	sqlParams.valueList.add(organizerName);
        }
        if(null != userInfo && userInfo.getOrganizerId() != null && !userInfo.getOrganizerId().equals(-1L)) {
        	if(findChildUsers == 1){
        		sqlParams.querySql.append(" AND a.organizer_id in(select distinct(ORGANIZER_CHILD_ID) from sys_organizer_recur where organizer_id=:organizerId ) ");
        		sqlParams.paramsList.add("organizerId");
        		sqlParams.valueList.add(userInfo.getOrganizerId());
        	}else{
        		sqlParams.querySql.append(" AND a.organizer_id = :organizerId ");
        		sqlParams.paramsList.add("organizerId");
        		sqlParams.valueList.add(userInfo.getOrganizerId());
        	}
        }
        if(null != userInfo && userInfo.getTenantsId() != null && !userInfo.getTenantsId().equals(-1L)) {
        	sqlParams.querySql.append(" AND a.TENANTS_ID = :tenantsId ");
        	sqlParams.paramsList.add("tenantsId");
        	sqlParams.valueList.add(userInfo.getTenantsId());
        }
		if(null != userInfo && !StringUtils.isBlank(userInfo.getIsAdmin())) {
			sqlParams.querySql.append(" AND a.IS_ADMIN = :isAdmin ");
			sqlParams.paramsList.add("isAdmin");
			sqlParams.valueList.add(userInfo.getIsAdmin());
        }
		if(!StringUtils.isBlank(roleId)) {
		    List<String> roleIdList = StringUtils.splitToList(roleId, ",");
		    sqlParams.querySql.append(" AND a.user_id in(select user_id from sys_user_role where role_id in (:roleIdList) ) ");
//			sqlParams.querySql.append(" AND a.user_id in(select user_id from sys_user_role where role_id=:roleId) ");
			sqlParams.paramsList.add("roleIdList");
			sqlParams.valueList.add(roleIdList);
        }
        if(null != userInfo && !StringUtils.isBlank(userInfo.getUserType())){
        	sqlParams.querySql.append(" AND a.USER_TYPE = :userType ");
        	sqlParams.paramsList.add("userType");
        	sqlParams.valueList.add(userInfo.getUserType());
        }
        if(null != userInfo && !StringUtils.isBlank(userInfo.getStatus())){
            sqlParams.querySql.append(" AND a.STATUS = :status ");
            sqlParams.paramsList.add("status");
            sqlParams.valueList.add(userInfo.getStatus());
        }
        if(null != userInfo && !StringUtils.isBlank(userInfo.getMobile())) {
            sqlParams.querySql.append(getLikeSql("a.MOBILE", ":mobile"));
            sqlParams.paramsList.add("mobile");
            sqlParams.valueList.add(userInfo.getMobile());
        }
        if(null != userInfo && userInfo.getCreateBy() != null){
        	sqlParams.querySql.append(" AND a.CREATE_BY = :createBy ");
        	sqlParams.paramsList.add("createBy");
        	sqlParams.valueList.add(userInfo.getCreateBy());
        }
        //过滤
        if(!StringUtils.isBlank(notInAccount)) {
            List<String> accountList = StringUtils.splitToList(notInAccount, ",");
            sqlParams.querySql.append(" AND a.user_code not in(:accountList) ");
            sqlParams.paramsList.add("accountList");
            sqlParams.valueList.add(accountList);
        }
        return sqlParams;
	}
	
	/**
	 * 查找当前组织当前租户下的用户（包括子组织）
	 * @param userInfo
	 * @return
	 */
	public List<Map<String, Object>> getUserKeyVal(UserInfo userInfo) {
		//组合查询条件
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append("SELECT u.USER_ID as ID, u.USER_NAME as NAME FROM sys_user_info u WHERE IS_VALID = 'Y' ");
		if(null != userInfo && userInfo.getOrganizerId() != null && !userInfo.getOrganizerId().equals(-1L)) {
			sqlParams.querySql.append(" AND u.organizer_id in(select distinct(ORGANIZER_CHILD_ID) from sys_organizer_recur where organizer_id=:organizerId ) ");
			sqlParams.paramsList.add("organizerId");
			sqlParams.valueList.add(userInfo.getOrganizerId());
        }
        if(null != userInfo && userInfo.getTenantsId() != null && !userInfo.getTenantsId().equals(-1L)) {
        	sqlParams.querySql.append(" AND u.TENANTS_ID =:tenantsId ");
        	sqlParams.paramsList.add("tenantsId");
        	sqlParams.valueList.add(userInfo.getTenantsId());
        }
        if(null != userInfo && !StringUtils.isBlank(userInfo.getUserType())){
        	sqlParams.querySql.append(" AND u.USER_TYPE=:userType ");
        	sqlParams.paramsList.add("userType");
        	sqlParams.valueList.add(userInfo.getUserType());
        }
        sqlParams.querySql.append(" ORDER BY USER_ID ASC ");
		return getResultList(sqlParams);
	}

}
