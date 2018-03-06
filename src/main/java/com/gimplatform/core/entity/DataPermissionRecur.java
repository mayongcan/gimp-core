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
 * 数据权限递归子表对象实体类
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_data_permission_recur")
public class DataPermissionRecur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NonNull
    @Column(name = "PERMISSION_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long permissionId;

    @Id
    @NonNull
    @Column(name = "PERMISSION_CHILD_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long permissionChildId;

}
