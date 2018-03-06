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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短信
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_sms_info")
public class SmsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 10, scale = 0)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SmsInfoIdGenerator")
    @TableGenerator(name = "SmsInfoIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_SMS_INFO_PK", allocationSize = 1)
    private Long id;

    @Column(name = "PHONE", length = 256)
    private String phone;

    @Column(name = "SMS_CODE", length = 64)
    private String smsCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;

    @Column(name = "MESSAGE_ID", length = 256)
    private String messageId;

    @Column(name = "TEMPLATE_ID", length = 256)
    private String templateId;

    @Column(name = "IS_VALID", length = 2)
    private String isValid;

}
