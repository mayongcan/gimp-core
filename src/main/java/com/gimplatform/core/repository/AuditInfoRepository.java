package com.gimplatform.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.AuditInfo;

@Repository
public interface AuditInfoRepository extends JpaRepository<AuditInfo, Long>, JpaSpecificationExecutor<AuditInfo> {
	
	/**
	 * 删除字典信息（将字典信息的IS_VALID设置为N）
	 * @param isValid
	 * @param userId
	 * @param date
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_audit_info "
			+ "SET IS_VALID = :isValid "
			+ "WHERE ID IN (:idList)", nativeQuery = true)
	public void delEntity(@Param("isValid")String isValid, @Param("idList")List<Long> idList);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_audit_info "
			+ "SET AUDIT_STATUS = '3' "
			+ "WHERE ID IN (:idList)", nativeQuery = true)
	public void auditNotPass(@Param("idList")List<Long> idList);
}
