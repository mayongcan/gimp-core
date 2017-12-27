package com.gimplatform.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * 用户代理字符串识别工具
 * @author zzd
 *
 */
public class UserAgentUtils {

	/**
	 * 获取用户代理对象
	 * @param request
	 * @return
	 */
	public static UserAgent getUserAgent(HttpServletRequest request){
		return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
	}
	
	/**
	 * 获取设备类型
	 * @param request
	 * @return
	 */
	public static DeviceType getDeviceType(HttpServletRequest request){
		return getUserAgent(request).getOperatingSystem().getDeviceType();
	}
	
	/**
	 * 是否是PC
	 * @param request
	 * @return
	 */
	public static boolean isComputer(HttpServletRequest request){
		return DeviceType.COMPUTER.equals(getDeviceType(request));
	}

	/**
	 * 是否是手机
	 * @param request
	 * @return
	 */
	public static boolean isMobile(HttpServletRequest request){
		return DeviceType.MOBILE.equals(getDeviceType(request));
	}
	
	/**
	 * 是否是平板
	 * @param request
	 * @return
	 */
	public static boolean isTablet(HttpServletRequest request){
		return DeviceType.TABLET.equals(getDeviceType(request));
	}

	/**
	 * 是否是手机和平板
	 * @param request
	 * @return
	 */
	public static boolean isMobileOrTablet(HttpServletRequest request){
		DeviceType deviceType = getDeviceType(request);
		return DeviceType.MOBILE.equals(deviceType) || DeviceType.TABLET.equals(deviceType);
	}
	
	/**
	 * 获取浏览类型
	 * @param request
	 * @return
	 */
	public static Browser getBrowser(HttpServletRequest request){
		return getUserAgent(request).getBrowser();
	}
	
	/**
	 * 是否IE版本是否小于等于IE8
	 * @param request
	 * @return
	 */
	public static boolean isLteIE8(HttpServletRequest request){
		Browser browser = getBrowser(request);
		return Browser.IE5.equals(browser) || Browser.IE6.equals(browser)
				|| Browser.IE7.equals(browser) || Browser.IE8.equals(browser);
	}
	
	/**
	 * 获取客户端IP地址
	 * @param httpservletrequest
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest httpservletrequest) {
	    if (httpservletrequest == null)
	        return null;
	    String s = httpservletrequest.getHeader("X-Forwarded-For");
	    if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
	        s = httpservletrequest.getHeader("Proxy-Client-IP");
	    if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
	        s = httpservletrequest.getHeader("WL-Proxy-Client-IP");
	    if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
	        s = httpservletrequest.getHeader("HTTP_CLIENT_IP");
	    if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
	        s = httpservletrequest.getHeader("HTTP_X_FORWARDED_FOR");
	    if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
	        s = httpservletrequest.getRemoteAddr();
	    if ("127.0.0.1".equals(s) || "0:0:0:0:0:0:0:1".equals(s)){
	        try {
	            s = InetAddress.getLocalHost().getHostAddress();
	        }
	        catch (UnknownHostException unknownhostexception) {
	        }
	    }
	    return s;
	}
	
}
