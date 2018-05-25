package com.gimplatform.core.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.UserLogonRepositoryCustom;

/**
 * 用户信息DAO实现类
 * @author zzd
 *
 */
@Transactional
public class UserLogonRepositoryImpl extends BaseRepository implements UserLogonRepositoryCustom{
	
	//用户列表sql
	private static final String MYSQL_SQL_GET_LOCK_ACCOUNT = "SELECT a.user_name as \"userName\", a.email as \"email\", a.user_id as \"userId\", a.is_valid as \"isValid\", a.user_code as \"userCode\", "
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
			+ "WHERE a.is_valid = 'Y' AND d.faile_count >= 5 ";
	
	private static final String MYSQL_SQL_GET_LOCK_ACCOUNT_COUNT = "SELECT count(1) as count "
			+ "FROM sys_user_info a left join sys_organizer_info b on (a.organizer_id = b.organizer_id) "
					+ "left join sys_tenants_info c on (a.tenants_id = c.tenants_id) "
					+ "left join sys_user_logon d on (a.user_id = d.user_id) "
					+ "left join sys_organizer_post e on (a.user_id = e.SUBORDINATE_USER_ID ) "
					+ "left join sys_user_info f on (e.SUPERIOR_USER_ID = f.user_id ) "
			+ "WHERE a.is_valid = 'Y' AND d.faile_count >= 5 ";

	//用户列表sql
	private static final String ORACLE_SQL_GET_LOCK_ACCOUNT = "SELECT a.user_name as \"userName\", a.email as \"email\", a.user_id as \"userId\", a.is_valid as \"isValid\", a.user_code as \"userCode\", "
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
			+ "WHERE a.is_valid = 'Y' AND d.faile_count >= 5 ";
	
	private static final String ORACLE_SQL_GET_LOCK_ACCOUNT_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM sys_user_info a left join sys_organizer_info b on (a.organizer_id = b.organizer_id) "
					+ "left join sys_tenants_info c on (a.tenants_id = c.tenants_id) "
					+ "left join sys_user_logon d on (a.user_id = d.user_id) "
					+ "left join sys_organizer_post e on (a.user_id = e.SUBORDINATE_USER_ID ) "
					+ "left join sys_user_info f on (e.SUPERIOR_USER_ID = f.user_id ) "
			+ "WHERE a.is_valid = 'Y' AND d.faile_count >= 5 ";
	
	public UserLogonRepositoryImpl(){
		sqlMap = new HashMap<String, String>();
		sqlMap.put("MYSQL_SQL_GET_LOCK_ACCOUNT", MYSQL_SQL_GET_LOCK_ACCOUNT);
		sqlMap.put("MYSQL_SQL_GET_LOCK_ACCOUNT_COUNT", MYSQL_SQL_GET_LOCK_ACCOUNT_COUNT);
		sqlMap.put("ORACLE_SQL_GET_LOCK_ACCOUNT", ORACLE_SQL_GET_LOCK_ACCOUNT);
		sqlMap.put("ORACLE_SQL_GET_LOCK_ACCOUNT_COUNT", ORACLE_SQL_GET_LOCK_ACCOUNT_COUNT);
	}

	public List<Map<String, Object>> getLockAccount(UserInfo userInfo, int pageIndex, int pageSize) {
		//生成查询条件
		SqlParams sqlParams = genLockAccountWhere(getSqlContent("SQL_GET_LOCK_ACCOUNT"), userInfo);
		//添加分页和排序
		sqlParams = getPageableSql(sqlParams, pageIndex, pageSize, " a.USER_ID DESC ", " \"userId\" DESC ");
		return getResultList(sqlParams);
	}

	public int getLockAccountCount(UserInfo userInfo) {
		//生成查询条件
		SqlParams sqlParams = genLockAccountWhere(getSqlContent("SQL_GET_LOCK_ACCOUNT_COUNT"), userInfo);
		return getResultListTotalCount(sqlParams);
	}
	
	/**
	 * 生成查询条件
	 * @param sql
	 * @param userInfo
	 * @return
	 */
	private SqlParams genLockAccountWhere(String sql, UserInfo userInfo){
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(sql);
		//添加查询参数
		if(null != userInfo && userInfo.getTenantsId() != null && !userInfo.getTenantsId().equals(-1L)) {
			sqlParams.querySql.append(" AND a.TENANTS_ID =:tenantsId ");
			sqlParams.paramsList.add("tenantsId");
			sqlParams.valueList.add(userInfo.getTenantsId());
        }
        return sqlParams;
	}

}
