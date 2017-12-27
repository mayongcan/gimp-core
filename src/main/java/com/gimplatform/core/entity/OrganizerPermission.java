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
 * 用户角色对象关联表
 * @author zzd
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_organizer_data_permission")
public class OrganizerPermission implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @NonNull
	@Column(name = "ORGANIZER_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long organizerId;

	@Id
    @NonNull
	@Column(name = "PERMISSION_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long permissionId;

}
