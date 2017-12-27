/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gimplatform.core.annotation.CustomerDateAndTimeDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MessageUser数据库映射实体类
 * @version 1.0
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_message_user")
public class MessageUser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 唯一标识
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "MessageUserIdGenerator")
	@TableGenerator(name = "MessageUserIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_MESSAGE_USER_PK", allocationSize = 1)
	@Column(name = "ID", unique = true, nullable = false, precision = 10, scale = 0)
	private Long id;
	
	// 关联的消息ID
	@Column(name = "MSG_ID", precision = 10, scale = 0)
	private Long msgId;
	
	// 关联的用ID
	@Column(name = "USER_ID", precision = 10, scale = 0)
	private Long userId;
	
	// 是否已经发送
	@Column(name = "IS_SEND", length = 2)
	private String isSend;
	
	// 是否阅读
	@Column(name = "IS_READ", length = 2)
	private String isRead;
	
	// 用户阅读时间
	@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "READ_DATE")
	private Date readDate;
	
	// 消息发送时间
	@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SEND_DATE")
	private Date sendDate;
	
}
