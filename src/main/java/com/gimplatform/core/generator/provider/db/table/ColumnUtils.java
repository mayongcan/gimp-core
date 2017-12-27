package com.gimplatform.core.generator.provider.db.table;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gimplatform.core.generator.provider.db.table.model.Column;
import com.gimplatform.core.generator.utils.type.DatabaseDataTypesUtils;

public class ColumnUtils {

	public static String[] removeHibernateValidatorSpecialTags(String str) {
		if (str == null || str.trim().length() == 0)
			return new String[] {};
		return str.trim().replaceAll("@", "").replaceAll("\\(.*?\\)", "").trim().split("\\s+");
	}

	/**
	 * 得到JSR303 bean validation的验证表达式
	 * 
	 * @param c
	 * @return
	 */
	public static String getHibernateValidatorExpression(Column c) {
		if (!c.isPk() && !c.isNullable()) {
			if (DatabaseDataTypesUtils.isString(c.getJavaType())) {
				return "@NotBlank " + getNotRequiredHibernateValidatorExpression(c);
			} else {
				return "@NotNull " + getNotRequiredHibernateValidatorExpression(c);
			}
		} else {
			return getNotRequiredHibernateValidatorExpression(c);
		}
	}

	public static String getNotRequiredHibernateValidatorExpression(Column c) {
		String result = "";
		if (c.getSqlName().indexOf("mail") >= 0) {
			result += "@Email ";
		}
		if (DatabaseDataTypesUtils.isString(c.getJavaType())) {
			result += String.format("@Length(max=%s)", c.getSize());
		}
		if (DatabaseDataTypesUtils.isIntegerNumber(c.getJavaType())) {
			String javaType = DatabaseDataTypesUtils.getPreferredJavaType(c.getSqlType(), c.getSize(),
					c.getDecimalDigits());
			if (javaType.toLowerCase().indexOf("short") >= 0) {
				result += " @Max(" + Short.MAX_VALUE + ")";
			} else if (javaType.toLowerCase().indexOf("byte") >= 0) {
				result += " @Max(" + Byte.MAX_VALUE + ")";
			}
		}
		return result.trim();
	}

	/**
	 * 得到validation的验证表达式
	 * 
	 * @param c
	 * @return
	 */
	public static String getRapidValidation(Column c) {
		String result = "";
		if (c.getSqlName().indexOf("mail") >= 0) {
			result += "validate-email ";
		}
		if (DatabaseDataTypesUtils.isFloatNumber(c.getJavaType())) {
			result += "validate-number ";
		}
		if (DatabaseDataTypesUtils.isIntegerNumber(c.getJavaType())) {
			result += "validate-integer ";
			if (c.getJavaType().toLowerCase().indexOf("short") >= 0) {
				result += "max-value-" + Short.MAX_VALUE;
			} else if (c.getJavaType().toLowerCase().indexOf("integer") >= 0) {
				result += "max-value-" + Integer.MAX_VALUE;
			} else if (c.getJavaType().toLowerCase().indexOf("byte") >= 0) {
				result += "max-value-" + Byte.MAX_VALUE;
			}
		}
		return result;
	}
	

	/**
	 * 将string转换为List<ColumnEnum> 格式为: "enumAlias(enumKey,enumDesc)"
	 */
	private static Pattern two = Pattern.compile("(.*)\\((.*)\\)");
	private static Pattern three = Pattern.compile("(.*)\\((.*),(.*)\\)");

	public static List<EnumMetaDada> string2EnumMetaData(String data) {
		if (data == null || data.trim().length() == 0)
			return new ArrayList<EnumMetaDada>();
		List<EnumMetaDada> list = new ArrayList<EnumMetaDada>();
		Pattern p = Pattern.compile("\\w+\\(.*?\\)");
		Matcher m = p.matcher(data);
		while (m.find()) {
			String str = m.group();
			Matcher three_m = three.matcher(str);
			if (three_m.find()) {
				list.add(new EnumMetaDada(three_m.group(1), three_m.group(2), three_m.group(3)));
				continue;
			}
			Matcher two_m = two.matcher(str);
			if (two_m.find()) {
				list.add(new EnumMetaDada(two_m.group(1), two_m.group(1), two_m.group(2)));
				continue;
			}
			throw new IllegalArgumentException(
					"error enumString format:" + data + " expected format:F(1,Female);M(0,Male) or F(Female);M(Male)");
		}
		return list;
	}
	

	/**
	 * public enum ${enumClassName} { ${enumAlias}(${enumKey},${enumDesc});
	 * private String key; private String value; }
	 */
	public static class EnumMetaDada {
		private String enumAlias;
		private String enumKey;
		private String enumDesc;

		public EnumMetaDada(String enumAlias, String enumKey, String enumDesc) {
			super();
			this.enumAlias = enumAlias;
			this.enumKey = enumKey;
			this.enumDesc = enumDesc;
		}

		public String getEnumAlias() {
			return enumAlias;
		}

		public String getEnumKey() {
			return enumKey;
		}

		public String getEnumDesc() {
			return enumDesc;
		}
	}
}
