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
 * 客户端版本
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_audit_info")
public class AuditInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 10, scale = 0)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AuditInfoIdGenerator")
    @TableGenerator(name = "AuditInfoIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_AUDIT_INFO_PK", allocationSize = 1)
    private Long id;

    @Column(name = "AUDIT_ID", nullable = false, precision = 10, scale = 0)
    private Long auditId;

    @Column(name = "AUDIT_TYPE", length = 2)
    private String auditType;

    @Column(name = "AUDIT_STATUS", length = 2)
    private String auditStatus;

    @Column(name = "EDIT_STATUS", length = 2)
    private String editStatus;

    @Column(name = "EDIT_CACHE", length = 1536)
    private String editCache;

    @Column(name = "CREATE_BY", nullable = false, precision = 10, scale = 0)
    private Long createBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;

    @Column(name = "IS_VALID", length = 2)
    private String isValid;

}
