/*
 * Copyright(c) 2018 gimplatform(通用信息管理平台) All rights reserved.
 */
package com.gimplatform.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GenCode数据库映射实体类
 * @version 1.0
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_gen_code")
public class GenCode implements Serializable {

    private static final long serialVersionUID = 1L;

    // 唯一标识
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "GenCodeIdGenerator")
    @TableGenerator(name = "GenCodeIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_GEN_CODE_PK", allocationSize = 1)
    @Column(name = "CODE_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long codeId;

    // 系统名称
    @Column(name = "SYS_NAME", length = 128)
    private String sysName;

    // 模块名称
    @Column(name = "MODULE_NAME", length = 128)
    private String moduleName;

    // 基础包路径
    @Column(name = "BASE_PACKAGE", length = 128)
    private String basePackage;

    // 模块包路径
    @Column(name = "SUB_PACKAGE", length = 128)
    private String subPackage;

    // 数据库连接
    @Column(name = "JDBC_DRIVER", length = 50)
    private String jdbcDriver;

    //
    @Column(name = "JDBC_URL", length = 128)
    private String jdbcUrl;

    //
    @Column(name = "JDBC_USERNAME", length = 50)
    private String jdbcUsername;

    //
    @Column(name = "JDBC_PASSWORD", length = 50)
    private String jdbcPassword;

    // 数据表名
    @Column(name = "TABLE_NAME", length = 50)
    private String tableName;

    //
    @Column(name = "TABLE_DESC", length = 128)
    private String tableDesc;

    // 数据表栏
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "TABLE_COLUMN")
    private String tableColumn;

    // 显示的表单类型:1列表，2树
    @Column(name = "PAGE_TYPE", length = 2)
    private String pageType;

    //
    @Column(name = "RESTFUL_PATH", length = 128)
    private String restfulPath;

    //
    @Column(name = "PAGE_PATH", length = 128)
    private String pagePath;

    // 树列表相关信息
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "TREE_INFO")
    private String treeInfo;

    // 创建人
    @Column(name = "CREATE_BY", precision = 10, scale = 0)
    private Long createBy;

    // 创建日期
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;

    // 是否有效
    @Column(name = "IS_VALID", length = 2)
    private String isValid;

}
