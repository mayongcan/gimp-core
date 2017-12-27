package com.gimplatform.core.utils;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * oauth2工具类
 * @author zzd
 *
 */
public class OAuthUtils {

	/**
	 * 获取当前登录用户号
	 * @return
	 */
	public static String getCurrentLoginUserCode() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//判断是否为null(未经拦截的用户访问则会产生null值)
		if(authentication == null) return null;
		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		}
		if (principal instanceof Principal) {
			return ((Principal) principal).getName();
		}
		return String.valueOf(principal);
	}
}
