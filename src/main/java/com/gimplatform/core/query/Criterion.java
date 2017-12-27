package com.gimplatform.core.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 封装各种条件的接口
 * @author zzd
 *
 */
public interface Criterion {
	public enum Operator {
		EQ, NE, LIKE, GT, LT, GTE, LTE, AND, OR
	}
 
	public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder);
}