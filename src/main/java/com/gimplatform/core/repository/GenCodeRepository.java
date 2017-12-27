/*
 * Copyright(c) 2018 gimplatform All rights reserved.
 * distributed with this file and available online at
 */
package com.gimplatform.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.gimplatform.core.entity.GenCode;

/**
 * 实体资源类
 * @version 1.0
 * @author
 *
 */
@Repository
public interface GenCodeRepository extends JpaRepository<GenCode, Long>, JpaSpecificationExecutor<GenCode> {
	
	/**
	 * 删除信息（将信息的IS_VALID设置为N）
	 * @param isValid
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_gen_code "
			+ "SET IS_VALID=:isValid "
			+ "WHERE CODE_ID IN (:idList)", nativeQuery = true)
	public void delEntity(@Param("isValid")String isValid, @Param("idList")List<Long> idList);
	
	public List<GenCode> findByTableName(String tableName);
	
}