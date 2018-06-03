package com.gimplatform.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.common.Constants;
import com.gimplatform.core.entity.FuncInfo;
import com.gimplatform.core.entity.TenantsInfo;
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.entity.UserLogon;
import com.gimplatform.core.repository.FuncInfoRepository;
import com.gimplatform.core.repository.TenantsInfoRepository;
import com.gimplatform.core.repository.UserInfoRepository;
import com.gimplatform.core.repository.UserLogonRepository;
import com.gimplatform.core.service.OrganizerInfoService;
import com.gimplatform.core.service.UserInfoService;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.RestfulRetUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 用户信息服务类
 * 
 * @author zzd
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Autowired
	private UserLogonRepository userLogonRepository;

	@Autowired
	private OrganizerInfoService organizerInfoService;

	@Autowired
	private FuncInfoRepository funcInfoRepository;

	@Autowired
	private TenantsInfoRepository tenantsInfoRepository;

	/**
	 * 通过UserCode获取用户信息
	 * 
	 * @param userCode
	 * @return
	 */
	public UserInfo getByUserCode(String userCode) {
		List<UserInfo> list = userInfoRepository.findByUserCodeAndIsValid(userCode, Constants.IS_VALID_VALID);
		if (list.size() == 0)
			return null;
		else
			return list.get(0);
	}

	@Override
	public UserInfo getByUserId(Long userId) {
		if (userId == null)
			return new UserInfo();
		return userInfoRepository.findOne(userId);
	}

	/**
	 * 获取用户权限目录
	 * 
	 * @param userId
	 * @param tenantsId
	 * @return
	 */
	public List<FuncInfo> getUserFunc(UserInfo userInfo) {
		return funcInfoRepository.getUserFuncFolder(userInfo.getUserId(), userInfo.getOrganizerId(),
				userInfo.getTenantsId());
	}

	/**
	 * 通过folderId获取用户权限
	 * 
	 * @param userInfo
	 * @param folderId
	 * @return
	 */
	public List<FuncInfo> getUserFuncByFd(UserInfo userInfo, Long folderId) {
		return funcInfoRepository.getUserFuncListByFd(userInfo.getUserId(), userInfo.getOrganizerId(), folderId);
	}

	@Override
	public JSONObject getUserList(Pageable page, final UserInfo userInfo, Map<String, Object> params) {
		List<Map<String, Object>> list = userInfoRepository.getUserList(userInfo, params, page.getPageNumber(),
				page.getPageSize());
		int count = userInfoRepository.getUserListCount(userInfo, params);
		return RestfulRetUtils.getRetSuccessWithPage(list, count);
	}

	@Override
	public JSONObject addUser(UserInfo userInfo, UserInfo loginUser, Date beginDate, Date endDate) {
		userInfo.setIsValid(Constants.IS_VALID_VALID);
		if (userInfo.getOrganizerId() == null)
			userInfo.setOrganizerId(loginUser.getOrganizerId());
		if (userInfo.getTenantsId() == null)
			userInfo.setTenantsId(loginUser.getTenantsId());
		userInfo.setThemeName(Constants.THEME_DEFAULT);
		if (userInfo.getIsAdmin() == null || "".equals(userInfo.getIsAdmin()))
			userInfo.setIsAdmin(Constants.IS_VALID_INVALID);
		// 设置密码MD5
		if (StringUtils.isBlank(userInfo.getPassword())) {
			return RestfulRetUtils.getErrorMsg("20009", "新增用户的密码不能为空");
		} else {
			userInfo.setPassword(DigestUtils.md5DigestAsHex(userInfo.getPassword().getBytes()));
		}
		// OrganizerInfo organizerInfo =
		// organizerInfoRepository.findOne(userInfo.getOrganizerId());
		// if (organizerInfo != null && organizerInfo.getMaxUsers() < userInfoRepository
		// .getCountByOrganizerId(userInfo.getOrganizerId(), Constants.IS_VALID_VALID))
		// {
		// return RestfulRetUtils.getErrorMsg("20007", "当前组织所允许的用户数已达上限");
		// }
		if (userInfoRepository.findByUserCodeAndIsValid(userInfo.getUserCode(), Constants.IS_VALID_VALID).size() > 0) {
			return RestfulRetUtils.getErrorMsg("20008", "当前输入的账号已存在，请修改后重试！");
		}

		if (userInfo.getDeptId() != null)
			userInfo.setOrganizerId(organizerInfoService.getOrganizerByIdAndType(userInfo.getDeptId(), 1));
		userInfo.setOpenId(UUID.randomUUID().toString().replaceAll("-", ""));
		userInfo.setCreateDate(new Date());
		userInfo.setModifyDate(new Date());
		if (loginUser != null) {
			userInfo.setCreateBy(loginUser.getUserId());
			userInfo.setModifyBy(loginUser.getUserId());
		}
		userInfo.setUserType("1");
		userInfo = userInfoRepository.saveAndFlush(userInfo);
		// 保存登录记录
		addUserLogon(userInfo, beginDate, endDate);
		return RestfulRetUtils.getRetSuccess(userInfo);
	}

	@Override
	public JSONObject editUser(UserInfo userInfo, UserInfo loginUser, Date beginDate, Date endDate) {
		userInfo.setIsValid(Constants.IS_VALID_VALID);
		if (StringUtils.isBlank(userInfo.getPassword())) {
			userInfo.setPassword(null);
		} else {
			userInfo.setPassword(DigestUtils.md5DigestAsHex(userInfo.getPassword().getBytes()));
		}
		UserInfo userInfoInDb = userInfoRepository.findOne(userInfo.getUserId());
		if (userInfo.getDeptId() != null && !userInfo.getDeptId().equals(userInfoInDb.getDeptId()))
			userInfo.setOrganizerId(organizerInfoService.getOrganizerByIdAndType(userInfo.getDeptId(), 1));

		// 判断修改后的UserCode是否已存在于数据库
		if (!userInfoInDb.getUserCode().equals(userInfo.getUserCode()) && userInfoRepository
				.findByUserCodeAndIsValid(userInfo.getUserCode(), Constants.IS_VALID_VALID).size() > 0) {
			return RestfulRetUtils.getErrorMsg("20008", "当前输入的账号已存在，请修改后重试！");
		}

		BeanUtils.mergeBean(userInfo, userInfoInDb);

		userInfoInDb.setModifyDate(new Date());
		userInfoInDb.setModifyBy(loginUser.getUserId());
		userInfoRepository.save(userInfoInDb);
		// 保存登录记录
		userInfo.setTenantsId(userInfoInDb.getTenantsId());
		addUserLogon(userInfo, beginDate, endDate);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject delUser(String idsList, UserInfo loginUser) {
		String[] ids = idsList.split(",");
		List<Long> idList = new ArrayList<Long>();
		// 判断是否需要移除
		for (int i = 0; i < ids.length; i++) {
			UserInfo userInfo = userInfoRepository.getOne(StringUtils.toLong(ids[i]));
			if (userInfo != null && Constants.IS_VALID_VALID.equals(userInfo.getIsValid())
					&& !"ADMIN".equals(userInfo.getUserCode().toUpperCase())) {
				idList.add(StringUtils.toLong(ids[i]));
			}
			if (userInfo != null && userInfo.getIsAdmin().equals(Constants.IS_VALID_VALID)) {
				return RestfulRetUtils.getErrorMsg("20016", "不能删除租户管理员[" + userInfo.getUserCode() + "]");
			}
		}
		// 批量更新（设置IsValid 为N）
		if (idList.size() > 0)
			userInfoRepository.delUser(Constants.IS_VALID_INVALID, loginUser.getUserId(), new Date(), idList);
		return RestfulRetUtils.getRetSuccess();
	}

	/**
	 * 保存用户登录信息
	 * 
	 * @param userInfo
	 * @param beginDate
	 * @param endDate
	 */
	public UserLogon addUserLogon(UserInfo userInfo, Date beginDate, Date endDate) {
		if (userInfo == null)
			return new UserLogon();
		UserLogon userLogon = userLogonRepository.findOne(userInfo.getUserId());
		if (userLogon == null)
			userLogon = new UserLogon();
		TenantsInfo tenantsInfo = tenantsInfoRepository.getOne(userInfo.getTenantsId());
		userLogon.setUserId(userInfo.getUserId());
		userLogon.setValidBeginDate(tenantsInfo.getBeginDate());
		userLogon.setValidEndDate(tenantsInfo.getEndDate());
		userLogon.setFaileCount(0);
		userLogon.setLockBeginDate(null);
		userLogon.setLockEndDate(null);
		userLogon.setLockReason("");
		userLogon = userLogonRepository.save(userLogon);
		userLogonRepository.flush();
		return userLogon;
	}

	@Override
	public JSONObject setTenantsAdmin(UserInfo userInfo, List<Long> userIdList) {
		UserInfo tmpUser = null;
		for (Long userId : userIdList) {
			tmpUser = userInfoRepository.findOne(userId);
			if (tmpUser != null) {
				if (Constants.IS_VALID_VALID.equals(tmpUser.getIsAdmin()))
					tmpUser.setIsAdmin(Constants.IS_VALID_INVALID);
				else
					tmpUser.setIsAdmin(Constants.IS_VALID_VALID);
				userInfoRepository.save(tmpUser);
			}
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject updatePasswordByMobile(UserInfo userInfo) {
		userInfo.setPassword(DigestUtils.md5DigestAsHex(userInfo.getPassword().getBytes()));
		userInfoRepository.updatePasswordByMobile(userInfo.getPassword(), userInfo.getPhone());
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject updatePasswordByUserCodeAndMobile(String userCode, String mobile, String password) {
		password = DigestUtils.md5DigestAsHex(password.getBytes());
		userInfoRepository.updatePasswordByUserCodeAndMobile(password, userCode, mobile);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject updatePasswordByUserId(UserInfo userInfo) {
		userInfo.setPassword(DigestUtils.md5DigestAsHex(userInfo.getPassword().getBytes()));
		userInfoRepository.updatePasswordByUserId(userInfo.getPassword(), userInfo.getUserId());
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject updatePassword(Long userId, String newPassword, String oldPassword) {
		UserInfo userInfo = userInfoRepository.getOne(userId);
		if (userInfo == null) {
			return RestfulRetUtils.getErrorMsg("20008", "用户标识不正确！");
		}
		String oldPasswordMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
		String password = DigestUtils.md5DigestAsHex(newPassword.getBytes());
		if (!oldPasswordMd5.equals(userInfo.getPassword())) {
			return RestfulRetUtils.getErrorMsg("20008", "旧密码输入不正确！");
		}
		userInfoRepository.updatePasswordByUserId(password, userId);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject updatePayPassword(Long userId, String payPassword) {
		userInfoRepository.updatePayPassword(payPassword, userId);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject updateSafetyPassword(Long userId, String safetyPassword) {
		userInfoRepository.updateSafetyPassword(safetyPassword, userId);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject dimission(List<Long> idList) {
		userInfoRepository.dimission("N", idList);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject getUserKeyVal(UserInfo userInfo) {
		return RestfulRetUtils.getRetSuccess(userInfoRepository.getUserKeyVal(userInfo));
	}

	// /**
	// * 列表转String
	// * @param list
	// * @param split
	// * @return
	// */
	// private String listToString(List<String> list, String split){
	// String retVal = "";
	// if(list == null) return retVal;
	// if(StringUtils.isBlank(split)) split = ",";
	// for(int i = 0; i < list.size(); i++){
	// retVal += list.get(i) + split;
	// }
	// if(StringUtils.isNotBlank(retVal)) retVal = retVal.substring(0,
	// retVal.length() - 1);
	// return retVal;
	// }

	@Override
	public JSONObject addOrgainzerPost(UserInfo userInfo, Long superiorUserId, List<Long> userIdList) {
		// 先删除所有的绑定，再重新绑定
		userInfoRepository.delOrgainzerPost(superiorUserId);
		for (Long userId : userIdList) {
			// 删除下属的旧上司
			userInfoRepository.delOrgainzerPostBySubordinateUserId(userId);
			// 保存新上司
			userInfoRepository.saveOrgainzerPost(superiorUserId, userId);
		}
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject delOrgainzerPost(UserInfo userInfo, Long superiorUserId, List<Long> userIdList) {
		userInfoRepository.delOrgainzerPostBySuperiorUserId(superiorUserId, userIdList);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject getSubordinateList(Long superiorUserId) {
		return RestfulRetUtils.getRetSuccess(userInfoRepository.getSubordinateList(superiorUserId));
	}

	@Override
	public void updateBeginDateByTenantsId(Long tenantsId, Date beginDate) {
		userInfoRepository.updateBeginDateByTenantsId(tenantsId, beginDate);
	}

	@Override
	public void updateEndDateByTenantsId(Long tenantsId, Date endDate) {
		userInfoRepository.updateEndDateByTenantsId(tenantsId, endDate);
	}

	@Override
	public JSONObject getLockAccount(Pageable page, UserInfo userInfo) {
		List<Map<String, Object>> list = userLogonRepository.getLockAccount(userInfo, page.getPageNumber(),
				page.getPageSize());
		int count = userLogonRepository.getLockAccountCount(userInfo);
		return RestfulRetUtils.getRetSuccessWithPage(list, count);
	}

	@Override
	public JSONObject unlockAccount(String idsList, UserInfo loginUser) {
		String[] ids = idsList.split(",");
		List<Long> idList = new ArrayList<Long>();
		// 判断是否需要移除
		for (int i = 0; i < ids.length; i++) {
			idList.add(StringUtils.toLong(ids[i]));
		}
		// 批量更新（设置IsValid 为N）
		if (idList.size() > 0)
			userLogonRepository.unlockAccount(idList);
		return RestfulRetUtils.getRetSuccess();
	}

	@Override
	public JSONObject updateOpenIdByUserId(UserInfo userInfo) {
		userInfoRepository.updateOpenIdByUserId(userInfo.getOpenId(), userInfo.getUserId());
		return RestfulRetUtils.getRetSuccess();
	}
}
