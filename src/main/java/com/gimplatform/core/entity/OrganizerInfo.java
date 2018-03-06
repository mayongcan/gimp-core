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
 * 组织对象实体类
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_organizer_info")
public class OrganizerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ORGANIZER_ID", unique = true, nullable = false, precision = 10, scale = 0)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "OrganizerIdGenerator")
    @TableGenerator(name = "OrganizerIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_ORGANIZER_INFO_PK", allocationSize = 1)
    private Long organizerId;

    @Column(name = "TENANTS_ID", nullable = false, precision = 10, scale = 0)
    private Long tenantsId;

    @Column(name = "PARENT_ORG_ID", precision = 10, scale = 0)
    private Long parentOrgId;

    @Column(name = "ORGANIZER_NAME", nullable = false, length = 50)
    private String organizerName;

    @Column(name = "ORGANIZER_MEMO", length = 200)
    private String organizerMemo;

    @Column(name = "ORGANIZER_TYPE", length = 255)
    private Integer organizerType;

    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "ORGANIZER_CODE", length = 50)
    private String organizerCode;

    @Column(name = "AREA_CODE", length = 50)
    private String areaCode;

    @Column(name = "AREA_NAME", length = 128)
    private String areaName;

    @Column(name = "ORGANIZER_LEVEL", length = 2)
    private String organizerLevel;

    @Column(name = "ORGANIZER_FUNC", length = 128)
    private String organizerFunc;

    @Column(name = "PRINCIPLE", length = 50)
    private String principle;

    @Column(name = "PRINCIPLE_TEL", length = 50)
    private String principleTel;

    @Column(name = "MANAGER", length = 255)
    private String manager;

    @Column(name = "MANAGER_TEL", length = 255)
    private String managerTel;

    @Column(name = "EMAIL", length = 50)
    private String email;

    @Column(name = "FAX", length = 50)
    private String fax;

    @Column(name = "MAX_USERS", nullable = false, precision = 10, scale = 0)
    private Long maxUsers;

    @Column(name = "STATUS", nullable = false, precision = 2, scale = 0)
    private Long status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BEGIN_DATE", nullable = false, length = 19)
    private Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE", nullable = false, length = 19)
    private Date endDate;

    @Column(name = "NAME_FIRST_LETTER", length = 50)
    private String nameFirstLetter;

    @Column(name = "NAME_FULL_LETTER", length = 300)
    private String nameFullLetter;

    @Column(name = "ID_PATH", length = 50)
    private String idPath;

    @Column(name = "NAME_PATH", length = 500)
    private String namePath;

    @Column(name = "AUDIT_STATUS", length = 2)
    private String auditStatus;

    @Column(name = "EDIT_STATUS", length = 2)
    private String editStatus;

    @Column(name = "EDIT_CACHE", length = 1536)
    private String editCache;

    @Column(name = "CREATE_BY", precision = 10, scale = 0)
    private Long createBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;

    @Column(name = "MODIFY_BY", precision = 10, scale = 0)
    private Long modifyBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFY_DATE")
    private Date modifyDate;

    @Column(name = "IS_VALID", length = 2)
    private String isValid;
}
