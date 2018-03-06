package com.gimplatform.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ID生成对象实体类
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_tb_generator")
public class TbGenerator implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 10, scale = 0)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TBAutoIdGenerator")
    @TableGenerator(name = "TBAutoIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_TB_GENERATOR_PK", allocationSize = 1)
    private long id;

    @Column(name = "GEN_TABLE", length = 128)
    private String genTable;

    @Column(name = "GEN_PRIMARY_KEY", length = 128)
    private String genPrimaryKey;

    @Column(name = "GEN_NAME", nullable = false, length = 255)
    private String genName;

    @Column(name = "GEN_VALUE", nullable = false, precision = 10, scale = 0)
    private long genValue;

}
