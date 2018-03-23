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
 * 权限对象实体类
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_func_info")
public class FuncInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "FUNC_ID", unique = true, nullable = false, precision = 10, scale = 0)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "FuncIdGenerator")
    @TableGenerator(name = "FuncIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_FUNC_INFO_PK", allocationSize = 1)
    private Long funcId;

    @Column(name = "PARENT_FUNC_ID", precision = 10, scale = 0)
    private Long parentFuncId;

    @Column(name = "FUNC_NAME", nullable = false, length = 50)
    private String funcName;

    @Column(name = "FUNC_TYPE", precision = 10, scale = 0)
    private Long funcType;

    @Column(name = "FUNC_LEVEL", precision = 6, scale = 0)
    private Long funcLevel;

    @Column(name = "FUNC_LINK", length = 200)
    private String funcLink;

    @Column(name = "FUNC_FLAG", length = 50)
    private String funcFlag;

    @Column(name = "FUNC_ICON", length = 100)
    private String funcIcon;

    @Column(name = "DISP_POSITION", length = 10)
    private String dispPosition;

    @Column(name = "FUNC_DESC", length = 500)
    private String funcDesc;

    @Column(name = "IS_BASE", length = 2)
    private String isBase;

    @Column(name = "IS_SHOW", length = 2)
    private String isShow;

    @Column(name = "IS_BLANK", length = 2)
    private String isBlank;

    @Column(name = "DISP_ORDER", precision = 6, scale = 0)
    private Long dispOrder;

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
