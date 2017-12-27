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
 * 字典类型实体类
 * @author zzd
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_dict_type")
public class DictType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "DICT_TYPE_ID", unique = true, nullable = false, precision = 10, scale = 0)
	@GeneratedValue(strategy = GenerationType.TABLE,generator="DictTypeIdGenerator")
    @TableGenerator(name = "DictTypeIdGenerator",table="sys_tb_generator",pkColumnName="GEN_NAME",valueColumnName="GEN_VALUE",pkColumnValue="SYS_DICT_TYPE_PK",allocationSize=1)
	private Long dictTypeId;

	@Column(name = "NAME", nullable = false, length = 100)
	private String name;

	@Column(name = "VALUE", length = 100)
	private String value;

	@Column(name = "SHARE_TYPE", nullable = false, length = 2)
	private String shareType;

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

	@Column(name = "IS_VALID", nullable = false, length = 2)
	private String isValid;

}
