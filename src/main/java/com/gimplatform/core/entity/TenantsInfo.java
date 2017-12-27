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
 * 组织对象实体类
 * @author zzd
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_tenants_info")
public class TenantsInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TENANTS_ID", unique = true, nullable = false, precision = 10, scale = 0)
	@GeneratedValue(strategy = GenerationType.TABLE,generator="TenantsIdGenerator")
    @TableGenerator(name = "TenantsIdGenerator",table="sys_tb_generator",pkColumnName="GEN_NAME",valueColumnName="GEN_VALUE",pkColumnValue="SYS_TENANTS_INFO_PK",allocationSize=1)
	private Long tenantsId;

	@Column(name = "TENANTS_NAME", nullable = false, length = 50)
	private String tenantsName;

	@Column(name = "STATUS", nullable = false, precision = 2, scale = 0)
	private Long status;
	
	@Column(name = "IS_ROOT", nullable = false, length = 2)
	private String isRoot;

	@Column(name = "MAX_USERS", nullable = false, precision = 10, scale = 0)
	private Long maxUsers;

	@Column(name = "TENANTS_DESC", length = 500)
	private String tenantsDesc;

	@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BEGIN_DATE", nullable = false, length = 19)
	private Date beginDate;

	@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE", nullable = false, length = 19)
	private Date endDate;

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
