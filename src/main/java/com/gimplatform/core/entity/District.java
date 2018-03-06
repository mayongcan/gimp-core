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
 * 区域实体类
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_district")
public class District implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", unique = true, nullable = false, precision = 10, scale = 0)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "DistrictIdGenerator")
    @TableGenerator(name = "DistrictIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_DISTRICT_PK", allocationSize = 1)
    private Long id;

    @Column(name = "PARENT_ID", precision = 10, scale = 0)
    private Long parentId;

    @Column(name = "NAME", length = 270)
    private String name;

    @Column(name = "FIRST_LETTER", length = 3)
    private String firstLetter;

    @Column(name = "INITIALS", length = 30)
    private String initials;

    @Column(name = "PINYIN", length = 600)
    private String pinyin;

    @Column(name = "EXTRA", length = 60)
    private String extra;

    @Column(name = "SUFFIX", length = 15)
    private String suffix;

    @Column(name = "CODE", length = 30)
    private String code;

    @Column(name = "AREA_CODE", length = 30)
    private String areaCode;

    @Column(name = "DISP_ORDER")
    private Integer dispOrder;

}
