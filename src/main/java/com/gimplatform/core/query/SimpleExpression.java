package com.gimplatform.core.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.gimplatform.core.utils.StringUtils;

/**
 * 简单的处理类
 * @author zzd
 *
 */
public class SimpleExpression implements Criterion {
	 
	/**
	 * @Fields : 属性名
	 */
	private String fieldName;
	/**
	 * @Fields : 对应值
	 */
	private Object value;
	/**
	 * @Fields : 计算符
	 */
	private Operator operator;
 
	protected SimpleExpression(String fieldName, Object value, Operator operator) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
	}
 
	public String getFieldName() {
		return fieldName;
	}
 
	public Operator getOperator() {
		return operator;
	}
 
	public Object getValue() {
		return value;
	}
 
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		Path expression = null;
		if (fieldName.contains(".")) {
			String[] names = StringUtils.split(fieldName, ".");
			expression = root.get(names[0]);
			for (int i = 1; i < names.length; i++) {
				expression = expression.get(names[i]);
			}
		} else {
			expression = root.get(fieldName);
		}
 
		switch (operator) {
		case EQ:
			return builder.equal(expression, value);
		case NE:
			return builder.notEqual(expression, value);
		case LIKE:
			return builder.like(expression, "%" + value + "%");
		case LT:
			return builder.lessThan(expression, (Comparable) value);
		case GT:
			return builder.greaterThan(expression, (Comparable) value);
		case LTE:
			return builder.lessThanOrEqualTo(expression, (Comparable) value);
		case GTE:
			return builder.greaterThanOrEqualTo(expression, (Comparable) value);
		default:
			return null;
		}
	}
}