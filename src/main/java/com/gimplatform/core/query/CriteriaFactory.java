package com.gimplatform.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gimplatform.core.query.Criterion.Operator;
import com.gimplatform.core.utils.StringUtils;

/**
 * 查询条件工厂类
 * @author zzd
 *
 */
public class CriteriaFactory {
	 
	/**
	 * @Title : and
	 * @Description : 并且
	 */
	public static LogicalExpression and(Criterion... criterions) {
		return new LogicalExpression(criterions, Operator.AND);
	}
	 
	/**
	 * @Title : or
	 * @Description : 或者
	 */
	public static LogicalExpression or(Criterion... criterions) {
		return new LogicalExpression(criterions, Operator.OR);
	}
 
	/**
	 * @Title : equal
	 * @Description : 等于
	 */
	public static SimpleExpression equal(String fieldName, Object value) {
		return equal(fieldName, value, false);
	}
 
	/**
	 * @Title : equal
	 * @Description : 等于
	 */
	public static SimpleExpression equal(String fieldName, Object value,
			boolean canNull) {
		if (!canNull && NullUtil.isEmpty(value)) {
			return null;
		}
		return new SimpleExpression(fieldName, value, Operator.EQ);
	}
 
	/**
	 * @Title : greaterThan
	 * @Description : 大于
	 */
	public static SimpleExpression greaterThan(String fieldName, Object value) {
		return greaterThan(fieldName, value, false);
	}
 
	/**
	 * @Title : greaterThan
	 * @Description : 大于
	 */
	public static SimpleExpression greaterThan(String fieldName, Object value,
			boolean canNull) {
		if (!canNull && NullUtil.isEmpty(value)) {
			return null;
		}
		return new SimpleExpression(fieldName, value, Operator.GT);
	}
 
	/**
	 * @Title : greaterThanOrEqualTo
	 * @Description : 大于等于
	 */
	public static SimpleExpression greaterThanOrEqualTo(String fieldName, Object value) {
		return greaterThanOrEqualTo(fieldName, value, false);
	}
 
	/**
	 * @Title : greaterThanOrEqualTo
	 * @Description : 大于等于
	 */
	public static SimpleExpression greaterThanOrEqualTo(String fieldName, Object value,
			boolean canNull) {
		if (!canNull && NullUtil.isEmpty(value)) {
			return null;
		}
		return new SimpleExpression(fieldName, value, Operator.GTE);
	}
 
	/**
	 * @Title : in
	 * @Description : 包含于
	 */
	@SuppressWarnings("rawtypes")
	public static LogicalExpression in(String fieldName, Collection value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		List<SimpleExpression> expressions = new ArrayList<SimpleExpression>();
		for (Object obj : value) {
			expressions.add(new SimpleExpression(fieldName, obj, Operator.EQ));
		}
		return new LogicalExpression(
				expressions.toArray(new SimpleExpression[expressions.size()]),
				Operator.OR);
	}
 
	/**
	 * @Title : like
	 * @Description : 模糊匹配
	 */
	public static SimpleExpression like(String fieldName, String value) {
		return like(fieldName, value, false);
	}
 
	/**
	 * @Title : like
	 * @Description : 模糊匹配
	 */
	public static SimpleExpression like(String fieldName, String value,
			boolean canNull) {
		if (!canNull && StringUtils.isBlank(value)) {
			return null;
		}
		return new SimpleExpression(fieldName, value, Operator.LIKE);
	}
 
	/**
	 * @Title : lessThan
	 * @Description : 小于
	 */
	public static SimpleExpression lessThan(String fieldName, Object value) {
		return lessThan(fieldName, value, false);
	}
 
	/**
	 * @Title : lessThan
	 * @Description : 小于
	 */
	public static SimpleExpression lessThan(String fieldName, Object value,
			boolean canNull) {
		if (!canNull && NullUtil.isEmpty(value)) {
			return null;
		}
		return new SimpleExpression(fieldName, value, Operator.LT);
	}
 
	/**
	 * @Title : lessThanOrEqualTo
	 * @Description : 小于等于
	 */
	public static SimpleExpression lessThanOrEqualTo(String fieldName, Object value) {
		return lessThanOrEqualTo(fieldName, value, false);
	}
 
	/**
	 * @Title : lessThanOrEqualTo
	 * @Description : 小于等于
	 */
	public static SimpleExpression lessThanOrEqualTo(String fieldName, Object value,
			boolean canNull) {
		if (!canNull && NullUtil.isEmpty(value)) {
			return null;
		}
		return new SimpleExpression(fieldName, value, Operator.LTE);
	}
 
	/**
	 * @Title : ne
	 * @Description : 不等于
	 */
	public static SimpleExpression notEqual(String fieldName, Object value) {
		return notEqual(fieldName, value, false);
	}
 
	/**
	 * @Title : ne
	 * @Description : 不等于
	 */
	public static SimpleExpression notEqual(String fieldName, Object value,
			boolean canNull) {
		if (!canNull && NullUtil.isEmpty(value)) {
			return null;
		}
		return new SimpleExpression(fieldName, value, Operator.NE);
	}
}