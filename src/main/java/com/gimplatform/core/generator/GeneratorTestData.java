package com.gimplatform.core.generator;

import java.sql.Timestamp;

import com.gimplatform.core.utils.RandomUtils;

/**
 * 生成测试数据
 * @author zzd
 *
 */
public class GeneratorTestData {

	public String getDBUnitTestData(String columnName,String javaType, int size) {
		if(size <= 0) size = 2;
		
		int MAX_SIZE = 3;
		if(javaType.indexOf("Boolean") >= 0) {
			return "0";
		}
		if(javaType.indexOf("Timestamp") >= 0) {
			return new Timestamp(System.currentTimeMillis()).toString();
		}
		if(javaType.indexOf("java.sql.Date") >= 0) {
			return new java.sql.Date(System.currentTimeMillis()).toString();
		}
		if(javaType.indexOf("java.sql.Time") >= 0) {
			return  new java.sql.Time(System.currentTimeMillis()).toString();
		}
		if(javaType.indexOf("java.util.Date") >= 0) {
			return  new Timestamp(System.currentTimeMillis()).toString();
		}
		if(javaType.indexOf("String") >= 0) {
			if(size > columnName.length()) {
				int tempSize = Math.min(size - columnName.length(), MAX_SIZE);
				return columnName + RandomUtils.randomNumeric(tempSize);
			}
			return RandomUtils.randomNumeric(Math.min(size, MAX_SIZE));
		}
		if(isNumberType(javaType)){
			return RandomUtils.randomNumeric(Math.min(size, MAX_SIZE));
		}
		return "";
	}

	private static boolean isNumberType(String javaType) {
		javaType = javaType.toLowerCase();
		if(javaType.indexOf("byte") >= 0 
				|| javaType.indexOf("short") >= 0 
				|| javaType.indexOf("integer") >= 0 
				|| javaType.indexOf("int") >= 0 
				|| javaType.indexOf("long") >= 0 
				|| javaType.indexOf("double") >= 0 
				|| javaType.indexOf("bigdecimal") >= 0 
				|| javaType.indexOf("float") >= 0) {
			return true;
		}
		return false;
	}
	
}
