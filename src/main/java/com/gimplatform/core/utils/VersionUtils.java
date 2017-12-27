package com.gimplatform.core.utils;

import java.util.Comparator;

import com.gimplatform.core.common.VersionCode;

/**
 * 版本对比工具
 * @author zzd
 *
 */
public class VersionUtils implements Comparator<VersionCode>{

	@Override
	public int compare(VersionCode versionCode0, VersionCode versionCode1) {
		int flag = versionCode0.getVerOne().compareTo(versionCode1.getVerOne());
		int flag1 = 0;
		if (flag == 0) {
			flag1 = versionCode0.getVerTwo().compareTo(versionCode1.getVerTwo());
			if (flag1 == 0) {
				flag1 = versionCode0.getVerThree().compareTo(versionCode1.getVerThree());
			} else {
				return flag1;
			}
		} else {
			return flag;
		}
		return flag1;
	}

}
