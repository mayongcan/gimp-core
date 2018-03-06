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
 * MessageInfo数据库映射实体类
 * @version 1.0
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_message_info")
public class MessageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 唯一标识
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MessageInfoIdGenerator")
    @TableGenerator(name = "MessageInfoIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_MESSAGE_INFO_PK", allocationSize = 1)
    @Column(name = "MSG_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long msgId;

    // 消息标题
    @Column(name = "MSG_TITLE", length = 256)
    private String msgTitle;

    // 消息内容
    @Column(name = "MSG_CONTENT", length = 2000)
    private String msgContent;

    // 消息类型
    @Column(name = "MSG_TYPE", length = 2)
    private String msgType;

    // 消息图片
    @Column(name = "MSG_IMG", length = 1024)
    private String msgImg;

    // 消息附件
    @Column(name = "MSG_FILE", length = 1024)
    private String msgFile;

    // 发送时间
    @JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SEND_DATE")
    private Date sendDate;

    // 创建人
    @Column(name = "CREATE_BY", precision = 10, scale = 0)
    private Long createBy;

    // 创建时间
    @JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;

    // 是否有效
    @Column(name = "IS_VALID", length = 2)
    private String isValid;

}
