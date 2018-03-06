package com.gimplatform.core.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 角色权限关联表
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_role_func")
public class RoleFunc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NonNull
    @Column(name = "ROLE_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long roleId;

    @Id
    @NonNull
    @Column(name = "FUNC_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long funcId;

}
