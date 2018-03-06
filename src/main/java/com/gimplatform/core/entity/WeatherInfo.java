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
 * 短信
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_weather_info")
public class WeatherInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "WEATHER_ID", unique = true, nullable = false, precision = 10, scale = 0)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "WeatherInfoIdGenerator")
    @TableGenerator(name = "WeatherInfoIdGenerator", table = "sys_tb_generator", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "SYS_WEATHER_INFO_PK", allocationSize = 1)
    private Long weatherId;

    @Column(name = "CITY", length = 100)
    private String city;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "WEATHER_DATE")
    private Date weatherDate;

    @Column(name = "TEXT_DAY", length = 20)
    private String textDay;

    @Column(name = "CODE_DAY", length = 10)
    private String codeDay;

    @Column(name = "TEXT_NIGTH", length = 20)
    private String textNigth;

    @Column(name = "CODE_NIGTH", length = 10)
    private String codeNight;

    @Column(name = "HIGHT", length = 10)
    private String hight;

    @Column(name = "LOW", length = 10)
    private String low;

    @Column(name = "PRECIP", length = 10)
    private String precip;

    @Column(name = "WIND_DIRECTION", length = 50)
    private String windDirection;

    @Column(name = "WIND_DIRECTION_DEGREE", length = 20)
    private String windDirectionDegree;

    @Column(name = "WIND_SPEED", length = 20)
    private String windSpeed;

    @Column(name = "WIND_SCALE", length = 20)
    private String windScale;

}
