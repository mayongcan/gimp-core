package com.gimplatform.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.gimplatform.core.entity.OrganizerInfo;
import com.gimplatform.core.repository.custom.OrganizerInfoRepositoryCustom;

/**
 * 组织信息资源操作类
 * @author zzd
 *
 */
@Repository
public interface OrganizerInfoRepository extends JpaRepository<OrganizerInfo, Long>, OrganizerInfoRepositoryCustom, JpaSpecificationExecutor<OrganizerInfo> {
	
	
	/**
	 * 根据parentOrgId,isValid查找组织
	 * @param parentOrgId
	 * @param isValid
	 * @return
	 */
	public List<OrganizerInfo> findByParentOrgIdAndIsValid(Long parentOrgId, String isValid);
	

	public List<OrganizerInfo> findByParentOrgIdAndIsValidAndOrganizerType(Long parentOrgId, String isValid, Integer organizerType);
	

	public List<OrganizerInfo> findByManagerAndIsValid(String manager, String isValid);

	/**
	 * 根据organizerName,isValid,tenantsId查找组织
	 * @param organizerName
	 * @param isValid
	 * @param tenantsId
	 * @return
	 */
	public List<OrganizerInfo> findByOrganizerNameAndIsValidAndTenantsId(String organizerName, String isValid, Long tenantsId);

	/**
	 * 根据organizerName,isValid,tenantsId查找组织
	 * @param organizerName
	 * @param organizerType
	 * @param isValid
	 * @param tenantsId
	 * @return
	 */
	public List<OrganizerInfo> findByOrganizerNameAndOrganizerTypeAndIsValidAndTenantsId(String organizerName, Integer organizerType, String isValid, Long tenantsId);
	
	
	/**
	 * 通过租户ID获取根组织机构
	 * @param tenantsId
	 * @return
	 */
	@Query(value = "SELECT * FROM sys_organizer_info WHERE TENANTS_ID = :tenantsId AND PARENT_ORG_ID IS NULL", nativeQuery = true)
    public OrganizerInfo getOrganizerRootByTenantsId(@Param("tenantsId")Long tenantsId);  
	
	/**
	 * 删除组织信息（将租户信息的IS_VALID设置为N）
	 * @param isValid
	 * @param userId
	 * @param date
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_organizer_info "
			+ "SET IS_VALID = :isValid, MODIFY_BY = :userId, MODIFY_DATE = :date "
			+ "WHERE ORGANIZER_ID IN (:idList)", nativeQuery = true)
	public void delOrganizer(@Param("isValid")String isValid, @Param("userId")Long userId, @Param("date")Date date, @Param("idList")List<Long> idList);

	/**
	 * 查找组织列表
	 * @param isValid
	 * @param idList
	 * @return
	 */
	@Query(value = "SELECT * FROM sys_organizer_info WHERE IS_VALID = :isValid AND PARENT_ORG_ID IN (:idList) ", nativeQuery = true)
	public List<OrganizerInfo> getOrganizerByParentOrgId(@Param("isValid")String isValid, @Param("idList")List<Long> idList);
	

	/**
	 * 根据组织ID查找所有子ID
	 * @param organizerId
	 * @return
	 */
	@Query(value = "SELECT ext.ORGANIZER_CHILD_ID "
			+ "FROM sys_organizer_info org inner join sys_organizer_recur ext on org.ORGANIZER_ID = ext.ORGANIZER_ID "
			+ "WHERE org.ORGANIZER_ID = :organizerId", nativeQuery = true)
	public List<Object> getAllChildIdByOrganizerId(@Param("organizerId")Long organizerId);

	/**
	 * 根据组织ID查找部门负责人
	 * @param organizerId
	 * @return
	 */
	@Query(value = "select org.MANAGER as manager from sys_organizer_info org "
			+ "where org.ORGANIZER_ID in "
			+ "(select distinct(ORGANIZER_ID) from sys_organizer_recur where ORGANIZER_CHILD_ID = :organizerId ) "
			+ "and org.ORGANIZER_TYPE = 2", nativeQuery = true)
	public List<Object> getManagerByOrganizerId(@Param("organizerId")Long organizerId);
	

	/**
	 * 删除OrganizerRecur
	 * @param organizerChildId
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM sys_organizer_recur "
			+ "WHERE ORGANIZER_CHILD_ID = :organizerChildId ", nativeQuery = true)
	public void delOrganizerRecurByOrganizerChildId(@Param("organizerChildId")Long organizerChildId);
	
	/**
	 * 保存Organizer
	 * @param organizerId
	 * @param organizerChildId
	 */
	@Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_organizer_recur (ORGANIZER_ID, ORGANIZER_CHILD_ID) VALUES (:organizerId, :organizerChildId) ", nativeQuery = true)
	public void saveOrganizerRecur(@Param("organizerId")Long organizerId, @Param("organizerChildId")Long organizerChildId);
	
	/**
	 * 更新beginDate
	 * @param tenantsId
	 * @param beginDate
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_organizer_info SET BEGIN_DATE = :beginDate WHERE TENANTS_ID = :tenantsId", nativeQuery = true)
	public void updateBeginDateByTenantsId(@Param("tenantsId")Long tenantsId, @Param("beginDate")Date beginDate);

	/**
	 * 更新beginDate
	 * @param tenantsId
	 * @param endDate
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_organizer_info SET END_DATE = :endDate WHERE TENANTS_ID = :tenantsId", nativeQuery = true)
	public void updateEndDateByTenantsId(@Param("tenantsId")Long tenantsId, @Param("endDate")Date endDate);
	

	/**
	 * 审核不通过
	 * @param isValid
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_organizer_info "
			+ "SET AUDIT_STATUS = '3' "
			+ "WHERE ORGANIZER_ID IN (:idList)", nativeQuery = true)
	public void auditNotPass(@Param("idList")List<Long> idList);
}
