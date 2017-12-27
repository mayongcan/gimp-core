package com.gimplatform.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.FuncInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.entity.UserLogon;

/**
 * 用户信息服务类
 * 
 * @author zzd
 *
 */
public interface UserInfoService {

	/**
	 * 通过UserCode获取用户信息
	 * 
	 * @param userCode
	 * @return
	 */
	public UserInfo getByUserCode(String userCode);

	/**
	 * 获取用户权限目录
	 * 
	 * @param userId
	 * @param tenantsId
	 * @return
	 */
	public List<FuncInfo> getUserFunc(UserInfo userInfo);

	/**
	 * 通过folderId(用户权限目录)获取用户权限
	 * 
	 * @param userInfo
	 * @param folderId
	 * @return
	 */
	public List<FuncInfo> getUserFuncByFd(UserInfo userInfo, Long folderId);

	/**
	 * 获取返回分页数据的用户列表（单用户表查询）
	 * 
	 * @param page
	 * @param userInfo
	 * @param organizerName
	 * @param findChildUsers
	 * @param roleId
	 * @param sort
	 * @return
	 */
	public JSONObject getUserList(Pageable page, UserInfo userInfo, Map<String, Object> params);

	/**
	 * 新增用户
	 * 
	 * @param userInfo
	 * @param loginUser
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public JSONObject addUser(UserInfo userInfo, UserInfo loginUser, Date beginDate, Date endDate);

	/**
	 * 编辑用户
	 * 
	 * @param userInfo
	 * @param loginUser
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public JSONObject editUser(UserInfo userInfo, UserInfo loginUser, Date beginDate, Date endDate);

	/**
	 * 删除用户
	 * 
	 * @param idsList
	 * @param loginUser
	 * @return
	 */
	public JSONObject delUser(String idsList, UserInfo loginUser);

	/**
	 * 设置租户管理员
	 * 
	 * @param userInfo
	 * @param userIdList
	 * @return
	 */
	public JSONObject setTenantsAdmin(UserInfo userInfo, List<Long> userIdList);

	/**
	 * 密码重置
	 * 
	 * @param userInfo
	 * @return
	 */
	public JSONObject updatePasswordByMobile(UserInfo userInfo);

	/**
	 * 密码重置
	 * 
	 * @param userInfo
	 * @return
	 */
	public JSONObject updatePasswordByUserCodeAndMobile(String userCode, String mobile, String password);
	
	/**
	 * 密码重置
	 * 
	 * @param userInfo
	 * @return
	 */
	public JSONObject updatePasswordByUserId(UserInfo userInfo);
	
	/**
	 * 更新密码
	 * @param userId
	 * @param newPassword
	 * @param oldPassword
	 * @return
	 */
	public JSONObject updatePassword(Long userId, String newPassword, String oldPassword);

	/**
	 * 离职
	 * 
	 * @param userInfo
	 * @param userIdList
	 * @return
	 */
	public JSONObject dimission(List<Long> idList);
	
	/**
	 * 获取用户列表
	 * @return
	 */
	public JSONObject getUserKeyVal(UserInfo userInfo);
	
	/**
	 * 保存OrgainzerPost
	 * @param userInfo
	 * @param superiorUserId
	 * @param userIdList
	 * @return
	 */
	public JSONObject addOrgainzerPost(UserInfo userInfo, Long superiorUserId, List<Long> userIdList);
	
	/**
	 * 删除OrgainzerPost
	 * @param userInfo
	 * @param superiorUserId
	 * @param userIdList
	 * @return
	 */
	public JSONObject delOrgainzerPost(UserInfo userInfo, Long superiorUserId, List<Long> userIdList);
	
	/**
	 * 获取直属下级
	 * @param superiorUserId
	 * @return
	 */
	public JSONObject getSubordinateList(Long superiorUserId);
	
	/**
	 * 更新开始时间
	 * @param tenantsId
	 * @param beginDate
	 */
	public void updateBeginDateByTenantsId(Long tenantsId, Date beginDate);
	
	/**
	 * 更新开始时间
	 * @param tenantsId
	 * @param endDate
	 */
	public void updateEndDateByTenantsId(Long tenantsId, Date endDate);
	
	/**
	 * 保存登录信息
	 * @param userInfo
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public UserLogon addUserLogon(UserInfo userInfo, Date beginDate, Date endDate);
	

	/**
	 * 获取返回分页数据的用户列表（单用户表查询）
	 * @param page
	 * @param userInfo
	 * @return
	 */
	public JSONObject getLockAccount(Pageable page, UserInfo userInfo);
	
	/**
	 * 解锁账号
	 * @param idsList
	 * @param loginUser
	 * @return
	 */
	public JSONObject unlockAccount(String idsList, UserInfo loginUser);
}
