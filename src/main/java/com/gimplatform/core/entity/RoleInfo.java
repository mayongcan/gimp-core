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
 * 角色对象实体类
 * @author zzd
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_role_info")
public class RoleInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ROLE_ID", unique = true, nullable = false, precision = 10, scale = 0)
	@GeneratedValue(strategy = GenerationType.TABLE,generator="RoleIdGenerator")
    @TableGenerator(name = "RoleIdGenerator",table="sys_tb_generator",pkColumnName="GEN_NAME",valueColumnName="GEN_VALUE",pkColumnValue="SYS_ROLE_INFO_PK",allocationSize=1)
	private Long roleId;

	@Column(name = "ROLE_NAME", nullable = false, length = 50)
	private String roleName;

	@Column(name = "ROLE_MEMO", length = 100)
	private String roleMemo;

	@Column(name = "TENANTS_ID", nullable = false, precision = 10, scale = 0)
	private Long tenantsId;

	@Column(name = "ORGANIZER_ID", nullable = false, precision = 10, scale = 0)
	private Long organizerId;

	@Column(name = "IS_FIX", length = 2)
	private String isFix;

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
