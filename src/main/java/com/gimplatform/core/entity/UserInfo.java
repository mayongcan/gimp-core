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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户对象实体类
 * @author zzd
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user_info")
public class UserInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "USER_ID", unique = true, nullable = false, precision = 10, scale = 0)
	@GeneratedValue(strategy = GenerationType.TABLE,generator="UserIdGenerator")
    @TableGenerator(name = "UserIdGenerator",table="sys_tb_generator",pkColumnName="GEN_NAME",valueColumnName="GEN_VALUE",pkColumnValue="SYS_USER_INFO_PK",allocationSize=1)
	@Getter @Setter private Long userId;

	@Column(name = "USER_CODE", nullable = false, length = 50)
	private String userCode;

	@Column(name = "USER_NAME", nullable = false, length = 50)
	private String userName;

	@Column(name = "PASSWORD", nullable = false, length = 50)
	private String password;

	@Column(name = "SEX", length = 2)
	private String sex;
	
	// 证件类型
	@Column(name = "CREDENTIALS_TYPE", length = 2)
	private String credentialsType;
	
	// 证件号码
	@Column(name = "CREDENTIALS_NUM", length = 50)
	private String credentialsNum;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BIRTHDAY")
	private Date birthday;

	@Column(name = "USER_TYPE", length = 50)
	private String userType;

	@Column(name = "EMAIL", length = 50)
	private String email;
	
	@Column(name = "MOBILE", length = 20)
	private String mobile;
	
	@Column(name = "PHONE", length = 20)
	private String phone;
	
	@Column(name = "PHOTO", length = 500)
	private String photo;
	
	@Column(name = "ADDRESS", length = 500)
	private String address;
	
	@Column(name = "COMMISSION", precision = 10, scale = 2)
	private Double commission;
	
	@Column(name = "THEME_NAME", length = 20)
	private String themeName;

	@Column(name = "IS_ADMIN", length = 2)
	private String isAdmin;

	@Column(name = "DEPT_ID", precision = 10, scale = 0)
	private Long deptId;

	@Column(name = "OPEN_ID", length = 32)
	private String openId;

	@Column(name = "TENANTS_ID", nullable = false, precision = 10, scale = 0)
	private Long tenantsId;

	@Column(name = "ORGANIZER_ID", nullable = false, precision = 10, scale = 0)
	private Long organizerId;

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
