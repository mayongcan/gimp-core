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
 * 员工上下级关系表
 * @author zzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_organizer_post")
public class OrganizerPost implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NonNull
    @Column(name = "SUPERIOR_USER_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long superiorUserId;

    @Id
    @NonNull
    @Column(name = "SUBORDINATE_USER_ID", unique = true, nullable = false, precision = 10, scale = 0)
    private Long subordinateUserId;
}
