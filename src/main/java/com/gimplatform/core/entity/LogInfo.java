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
 * 日志对象实体类
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_log_info")
public class LogInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "LOG_ID", unique = true, nullable = false, precision = 10, scale = 0)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LogIdGenerator")
    @TableGenerator(name = "LogIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_LOG_INFO_PK", allocationSize = 1)
    private Long logId;

    @Column(name = "LOG_TYPE", nullable = false, length = 2)
    private String logType;

    @Column(name = "LOG_TITLE", nullable = false, length = 255)
    private String logTitle;

    @Column(name = "CREATE_BY", precision = 10, scale = 0)
    private Long createBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;

    @Column(name = "REMOTE_ADDR", length = 255)
    private String remoteAddr;

    @Column(name = "USER_AGENT", length = 512)
    private String userAgent;

    @Column(name = "REQUEST_URI", length = 255)
    private String reqeustUri;

    @Column(name = "METHOD", length = 20)
    private String method;

    @Column(name = "PARAMS")
    private String params;

    @Column(name = "EXCEPTION")
    private String exception;

    // 日志类型（1：接入日志；2：错误日志）
    public static final String TYPE_ACCESS = "1";
    public static final String TYPE_EXCEPTION = "2";

}
