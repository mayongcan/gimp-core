/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.MessageUser;
import com.gimplatform.core.repository.custom.MessageUserRepositoryCustom;

/**
 * 实体资源类
 * @version 1.0
 * @author
 *
 */
@Repository
public interface MessageUserRepository extends JpaRepository<MessageUser, Long>, JpaSpecificationExecutor<MessageUser>, MessageUserRepositoryCustom {

	/**
	 * 删除
	 * @param msgId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_message_user WHERE MSG_ID =:msgId", nativeQuery = true)
	public void delByMsgId(@Param("msgId")Long msgId);

	/**
	 * 更新消息为已发送
	 * @param msgId
	 */
	@Transactional
    @Modifying
    @Query(value = "UPDATE sys_message_user "
			+ "SET IS_SEND = '1', SEND_DATE = :sendDate "
			+ "WHERE MSG_ID = :msgId ", nativeQuery = true)
	public void updateMessageIsSend(@Param("msgId")Long msgId, @Param("sendDate")Date sendDate);

	/**
	 * 更新消息为已阅读
	 * @param msgId
	 */
	@Transactional
    @Modifying
    @Query(value = "UPDATE sys_message_user "
			+ "SET IS_READ = '1', READ_DATE = :readDate "
			+ "WHERE ID = :id ", nativeQuery = true)
	public void updateMessageIsRead(@Param("id")Long id, @Param("readDate")Date sendDate);
	
}