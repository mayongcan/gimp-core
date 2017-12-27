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
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_client_version")
public class ClientVersion implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CLIENT_ID", unique = true, nullable = false, precision = 10, scale = 0)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ClientVersionIdGenerator")
	@TableGenerator(name = "ClientVersionIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_CLIENT_VERSION_PK", allocationSize = 1)
	private Long clientId;

	@Column(name = "NAME", length = 256)
	private String name;

	@Column(name = "VERSION", length = 20)
	private String version;

	@Column(name = "VER_DESC", length = 256)
	private String verDesc;

	@Column(name = "FILE_SIZE", length = 256)
	private String fileSize;

	@Column(name = "URL", length = 256)
	private String url;

	@Column(name = "IS_OPTION", length = 64)
	private String isOption;

	@Column(name = "CREATE_BY", nullable = false, precision = 10, scale = 0)
	private Long createBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "IS_VALID", length = 2)
	private String isValid;

}
