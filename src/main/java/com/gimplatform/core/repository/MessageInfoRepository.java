/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.MessageInfo;
import com.gimplatform.core.repository.custom.MessageInfoRepositoryCustom;

/**
 * 实体资源类
 * @version 1.0
 * @author
 *
 */
@Repository
public interface MessageInfoRepository extends JpaRepository<MessageInfo, Long>, JpaSpecificationExecutor<MessageInfo>, MessageInfoRepositoryCustom {
	
	
	/**
	 * 删除信息（将信息的IS_VALID设置为N）
	 * @param isValid
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_message_info "
			+ "SET IS_VALID=:isValid "
			+ "WHERE MSG_ID IN (:idList)", nativeQuery = true)
	public void delEntity(@Param("isValid")String isValid, @Param("idList")List<Long> idList);

	/**
	 * 撤回消息
	 * @param isRevoke
	 * @param idList
	 */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE sys_message_info "
            + "SET IS_REVOKE = :isRevoke "
            + "WHERE MSG_ID IN (:idList)", nativeQuery = true)
    public void revokeMessage(@Param("isRevoke")String isRevoke, @Param("idList")List<Long> idList);
}