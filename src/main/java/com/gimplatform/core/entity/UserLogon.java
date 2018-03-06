package com.gimplatform.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录对象实体类
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user_logon")
public class UserLogon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "USER_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long userId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "VALID_BEGIN_DATE", nullable = false, length = 19)
    private Date validBeginDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "VALID_END_DATE", nullable = false, length = 19)
    private Date validEndDate;

    @Column(name = "FAILE_COUNT", length = 11)
    private Integer faileCount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LOCK_BEGIN_DATE", length = 19)
    private Date lockBeginDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LOCK_END_DATE", length = 19)
    private Date lockEndDate;

    @Column(name = "LOCK_REASON", length = 256)
    private String lockReason;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_LOGON_DATE", length = 19)
    private Date lastLogonDate;

    @Column(name = "LAST_LOGON_IP", length = 50)
    private String lastLogonIp;

    @Column(name = "ACCESS_IPADDRESS", length = 100)
    private String accessIpaddress;

    @Column(name = "LAST_LOGON_SOURCE", length = 50)
    private String lastLogonSource;

    @Column(name = "ONLINE_STATUS", length = 50)
    private String onlineStatus;
}
