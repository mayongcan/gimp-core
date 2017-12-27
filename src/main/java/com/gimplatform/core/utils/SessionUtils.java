package com.gimplatform.core.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gimplatform.core.entity.FuncInfo;
import com.gimplatform.core.entity.UserInfo;

/**
 * 获取存储在session里面值的工具
 * @author zzd
 *
 */
public class SessionUtils {

	private static Logger logger = LoggerFactory.getLogger(SessionUtils.class);
	//过期时间，设置为1天
	private static final int SESSION_SAVE_TIME = 24 * 60 * 60;			
	
	private static final String SESSION_PREFIX = "GIMP:SESSION:";
	
	private static final String SESSION_USER_INFO = "USER_INFO";
	
	private static final String SESSION_USER_FUNC_PREFIX = "USER_FUNC";
	
	private static final String SESSION_USER_DATA_PERMISSION = "USER_DATA_PERMISSION";
	
	private static final String SESSION_USER_ROOT_ORGANIZER_ID = "USER_ROOT_ORGANIZER_ID";
	
	private static final String SESSION_USER_TOP_ORGANIZER_ID = "USER_TOP_ORGANIZER_ID";

	/**
	 * 写入值
	 * @param key
	 * @param value
	 */
	public static void setValue(String key, Object value){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			RedisUtils.hsetObject(SESSION_PREFIX + userCode, key, value, SESSION_SAVE_TIME);
		}else{
			logger.warn("setValue:当前用户未登陆[Key=" + key + "]");
		}
	}

	/**
	 * 读取值
	 * @param key
	 * @return
	 */
	public static Object getValue(String key){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			return RedisUtils.hgetObject(SESSION_PREFIX + userCode, key);
		}else{
			logger.warn("getValue:当前用户未登陆[Key=" + key + "]");
		}
		return null;
	}

	/**
	 * 写入UserInfo到session
	 * @param userInfo
	 */
	public static void setUserInfo(UserInfo userInfo){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			RedisUtils.hsetObject(SESSION_PREFIX + userCode, SESSION_USER_INFO, userInfo, SESSION_SAVE_TIME);
		}else{
			logger.warn("setUserInfo:当前用户未登陆");
		}
	}

	/**
	 * 获取用户信息
	 * @return
	 */
	public static UserInfo getUserInfo(){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			return (UserInfo) RedisUtils.hgetObject(SESSION_PREFIX + userCode, SESSION_USER_INFO);
		}else{
			logger.warn("getUserInfo:当前用户未登陆");
		}
		return null;
	}

	/**
	 * 移除UserInfo
	 * @return
	 */
	public static void removeAll(){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			RedisUtils.del(SESSION_PREFIX + userCode);
		}else{
			logger.warn("removeAll:当前用户未登陆");
		}
	}

	/**
	 * 将当前登录的用户权限列表写入redis
	 * @param userFuncList
	 * @param funcPid
	 */
	public static void setUserFunc(List<FuncInfo> userFuncList, String funcPid){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			RedisUtils.hsetObject(SESSION_PREFIX + userCode, SESSION_USER_FUNC_PREFIX + ":" + funcPid, userFuncList, SESSION_SAVE_TIME);
		}else{
			logger.warn("setUserFunc:当前用户未登陆");
		}
	}

	/**
	 * 获取当前登录的用户权限列表
	 * @param funcPid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<FuncInfo> getUserFunc(String funcPid){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			return (List<FuncInfo>) RedisUtils.hgetObject(SESSION_PREFIX + userCode, SESSION_USER_FUNC_PREFIX + ":" + funcPid);
		}else{
			logger.warn("getUserFunc:当前用户未登陆");
		}
		return null;
	}

	/**
	 * 写入用户数据权限到session
	 * @param userInfo
	 */
	public static void setUserDataPermission(String dataPermissionIdList){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			RedisUtils.hsetObject(SESSION_PREFIX + userCode, SESSION_USER_DATA_PERMISSION, dataPermissionIdList, SESSION_SAVE_TIME);
		}else{
			logger.warn("setUserInfo:当前用户未登陆");
		}
	}

	/**
	 * 获取用户所具有的数据权限ID列表
	 * @return
	 */
	public static List<Long> getUserDataPermission(){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		List<Long> retList = new ArrayList<Long>();
		if(!StringUtils.isBlank(userCode)){
			String dataPermissionIdList = (String) RedisUtils.hgetObject(SESSION_PREFIX + userCode, SESSION_USER_DATA_PERMISSION);
			if(StringUtils.isBlank(dataPermissionIdList)) return retList;
			else{
				String[] array = dataPermissionIdList.split(",");
				for(String str : array){
					Long id = StringUtils.toLong(str, null);
					if(id != null){
						retList.add(id);
					}
				}
				return retList;
			} 
		}else{
			logger.warn("getUserInfo:当前用户未登陆");
		}
		return retList;
	}

	/**
	 * 写入用户所属的组织ID到session（组织类型为1）
	 * @param userInfo
	 */
	public static void setUserTopOrganizerId(Long organizerId){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			RedisUtils.hsetObject(SESSION_PREFIX + userCode, SESSION_USER_TOP_ORGANIZER_ID, organizerId, SESSION_SAVE_TIME);
		}else{
			logger.warn("setUserInfo:当前用户未登陆");
		}
	}

	/**
	 * 获取用户所属的组织ID（组织类型为1）
	 * @return
	 */
	public static Long getUserTopOrganizerId(){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		Long organizerId = null;
		if(!StringUtils.isBlank(userCode)){
			organizerId = (Long) RedisUtils.hgetObject(SESSION_PREFIX + userCode, SESSION_USER_TOP_ORGANIZER_ID);
		}else{
			logger.warn("getUserInfo:当前用户未登陆");
		}
		return organizerId;
	}

	/**
	 * 写入根组织ID
	 * @param userInfo
	 */
	public static void setUserRootOrganizerId(Long organizerId){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		if(!StringUtils.isBlank(userCode)){
			RedisUtils.hsetObject(SESSION_PREFIX + userCode, SESSION_USER_ROOT_ORGANIZER_ID, organizerId, SESSION_SAVE_TIME);
		}else{
			logger.warn("setUserInfo:当前用户未登陆");
		}
	}

	/**
	 * 读取根组织ID
	 * @return
	 */
	public static Long getUserRootOrganizerId(){
		String userCode = OAuthUtils.getCurrentLoginUserCode();
		Long organizerId = null;
		if(!StringUtils.isBlank(userCode)){
			organizerId = (Long) RedisUtils.hgetObject(SESSION_PREFIX + userCode, SESSION_USER_ROOT_ORGANIZER_ID);
		}else{
			logger.warn("getUserInfo:当前用户未登陆");
		}
		return organizerId;
	}
	
	/**
	 * 获取分页查询的分页大小，默认为15条
	 * @param request
	 * @return
	 */
	public static int getPageSize(HttpServletRequest request){
		int size = StringUtils.toInteger(request.getParameter("size"), 0);
		if(size == 0) return 15;
		//限制一次查询条数为5000
		if(size > 5000) return 5000;
		else return size;
	}
	
	/**
	 * 获取分页查询的当前页码
	 * @param request
	 * @return
	 */
	public static int getPageIndex(HttpServletRequest request){
		return StringUtils.toInteger(request.getParameter("page"), 0);
	}
}
