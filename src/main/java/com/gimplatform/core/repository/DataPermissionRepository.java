package com.gimplatform.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.DataPermission;
import com.gimplatform.core.repository.custom.DataPermissionRepositoryCustom;

/**
 * 用户信息资源操作类
 * @author zzd
 *
 */
@Repository
public interface DataPermissionRepository extends JpaRepository<DataPermission, Long>, JpaSpecificationExecutor<DataPermission>, DataPermissionRepositoryCustom{ 

	public List<DataPermission> findByPermissionNameAndTenantsIdAndOrganizerIdAndIsValid(String permissionName, Long tenantsId, Long organizerId, String isValid);

	/**
	 * 
	 * @param userCode
	 * @return
	 */
	@Query(value = "SELECT DISTINCT(dp.PERMISSION_ID), dp.* "
			+ "FROM sys_data_permission dp left join sys_user_data_permission udp on udp.PERMISSION_ID = dp.PERMISSION_ID "
				+ "left join sys_user_info u on u.USER_ID = udp.USER_ID "
			+ "WHERE u.user_code = :userCode AND u.IS_VALID = 'Y' "
			+ "ORDER BY dp.DISP_ORDER ASC, dp.PERMISSION_ID ASC", nativeQuery = true)
    public List<DataPermission> getListByUserCode(@Param("userCode")String userCode);  
	
	/**
	 * 
	 * @param userCode
	 * @return
	 */
	@Query(value = "SELECT DISTINCT(dp.PERMISSION_ID), dp.* "
			+ "FROM sys_data_permission dp left join sys_organizer_data_permission odp on odp.PERMISSION_ID = dp.PERMISSION_ID "
			+ "WHERE odp.organizer_id = :organizerId "
			+ "ORDER BY dp.DISP_ORDER ASC, dp.PERMISSION_ID ASC", nativeQuery = true)
    public List<DataPermission> getListByOrganizerId(@Param("organizerId")Long organizerId); 
	
	/**
	 * 获取树列表
	 * @param tenantsId
	 * @param organizerId
	 * @return
	 */
	@Query(value = "SELECT tb.* "
			+ "FROM sys_data_permission tb "
			+ "WHERE tb.IS_VALID = 'Y' AND tb.TENANTS_ID = :tenantsId AND tb.ORGANIZER_ID = :organizerId "
			+ "ORDER BY PARENT_ID, DISP_ORDER, PERMISSION_ID", nativeQuery = true)
    public List<DataPermission> getTreeList(@Param("tenantsId")Long tenantsId, @Param("organizerId")Long organizerId);  

	/**
	 * 获取树列表
	 * @param tenantsId
	 * @param organizerId
	 * @return
	 */
	@Query(value = "SELECT tb.* "
			+ "FROM sys_data_permission tb "
			+ "WHERE tb.IS_VALID = 'Y' AND tb.TENANTS_ID = :tenantsId "
			+ "ORDER BY PARENT_ID, DISP_ORDER, PERMISSION_ID", nativeQuery = true)
    public List<DataPermission> getTreeListByTenantsId(@Param("tenantsId")Long tenantsId);

	/**
	 * 根据父ID列表获取ID列表
	 * @param idList
	 * @return
	 */
	@Query(value = "SELECT * FROM sys_data_permission WHERE IS_VALID='Y' AND PARENT_ID IN (:idList)", nativeQuery = true)
    public List<DataPermission> getListByParentIds(@Param("idList")List<Long> idList); 

	/**
	 * 根据父ID获取ID列表
	 * @param idList
	 * @return
	 */
	@Query(value = "SELECT * FROM sys_data_permission WHERE IS_VALID='Y' AND PARENT_ID = :parentId ", nativeQuery = true)
    public List<DataPermission> getListByParentId(@Param("parentId")Long parentId); 
	
	/**
	 * 根据父ID列表获取ID列表
	 * @param idList
	 * @return
	 */
	@Query(value = "SELECT * "
			+ "FROM sys_data_permission "
			+ "WHERE IS_VALID='Y' AND PARENT_ID is null "
			+ "ORDER BY PARENT_ID, DISP_ORDER, PERMISSION_ID", nativeQuery = true)
    public List<DataPermission> getListByRoot();  
	
	/**
	 * 根据父ID列表获取ID列表
	 * @param idList
	 * @return
	 */
	@Query(value = "SELECT * "
			+ "FROM sys_data_permission "
			+ "WHERE IS_VALID='Y' AND PARENT_ID is null AND TENANTS_ID = :tenantsId  "
			+ "ORDER BY PARENT_ID, DISP_ORDER, PERMISSION_ID", nativeQuery = true)
    public List<DataPermission> getListByRootAndTenantsId(@Param("tenantsId")Long tenantsId); 
	
	/**
	 * 根据父ID列表获取ID列表
	 * @param idList
	 * @return
	 */
	@Query(value = "SELECT * "
			+ "FROM sys_data_permission "
			+ "WHERE IS_VALID='Y' AND PARENT_ID is null AND TENANTS_ID = :tenantsId AND ORGANIZER_ID = :organizerId "
			+ "ORDER BY PARENT_ID, DISP_ORDER, PERMISSION_ID", nativeQuery = true)
    public List<DataPermission> getListByRootAndTenantsIdAndOrganizerId(@Param("tenantsId")Long tenantsId, @Param("organizerId")Long organizerId); 
	
	/**
	 * 删除信息（将信息的IS_VALID设置为N）
	 * @param isValid
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_data_permission "
			+ "SET IS_VALID = :isValid "
			+ "WHERE PERMISSION_ID IN (:idList)", nativeQuery = true)
	public void delEntity(@Param("isValid")String isValid, @Param("idList")List<Long> idList);
	
	/**
	 * 更新组织ID
	 * @param organizerId
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_data_permission "
			+ "SET ORGANIZER_ID = :organizerId "
			+ "WHERE PERMISSION_ID IN (:idList)", nativeQuery = true)
	public void updateOrgainzerIdByParentId(@Param("organizerId")Long organizerId, @Param("idList")List<Long> idList);
	
	
	/**
	 * 删除UserDataPermission关联表
	 * @param permissionId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_user_data_permission WHERE PERMISSION_ID =:permissionId", nativeQuery = true)
	public void delUserDataPermissionByPermissionId(@Param("permissionId")Long permissionId);

	/**
	 * 删除UserDataPermission关联表
	 * @param permissionId
	 * @param idList
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_user_data_permission WHERE PERMISSION_ID =:permissionId AND USER_ID IN(:idList) ", nativeQuery = true)
	public void delUserDataPermissionByPermissionIdAndUserId(@Param("permissionId")Long permissionId, @Param("idList")List<Long> idList);

	/**
	 * 删除UserDataPermission关联表
	 * @param userId
	 * @param permissionId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_user_data_permission WHERE PERMISSION_ID =:permissionId AND USER_ID =:userId ", nativeQuery = true)
	public void delUserDataPermission(@Param("userId")Long userId, @Param("permissionId")Long permissionId);
	
	/**
	 * 保存UserDataPermission
	 * @param userId
	 * @param permissionId
	 */
	@Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_user_data_permission (USER_ID, PERMISSION_ID) VALUES (:userId, :permissionId) ", nativeQuery = true)
	public void saveUserDataPermission(@Param("userId")Long userId, @Param("permissionId")Long permissionId);

	/**
	 * 删除OrganizerDataPermissio关联表
	 * @param organizerId
	 * @param permissionId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_organizer_data_permission WHERE PERMISSION_ID =:permissionId AND ORGANIZER_ID =:organizerId ", nativeQuery = true)
	public void delOrganizerDataPermission(@Param("organizerId")Long organizerId, @Param("permissionId")Long permissionId);
	
	/**
	 * 删除OrganizerDataPermissio关联表
	 * @param permissionId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_organizer_data_permission WHERE ORGANIZER_ID =:organizerId", nativeQuery = true)
	public void delOrganizerDataPermissionByOrganizerId(@Param("organizerId")Long organizerId);

	/**
	 * 删除OrganizerDataPermissio关联表
	 * @param permissionId
	 * @param idList
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_organizer_data_permission WHERE PERMISSION_ID =:permissionId AND ORGANIZER_ID IN(:idList) ", nativeQuery = true)
	public void delOrganizerDataPermissionByPermissionIdAndOrganizerId(@Param("permissionId")Long permissionId, @Param("idList")List<Long> idList);

	/**
	 * 保存saveOrganizerDataPermission
	 * @param organizerId
	 * @param permissionId
	 */
	@Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_organizer_data_permission (ORGANIZER_ID, PERMISSION_ID) VALUES (:organizerId, :permissionId) ", nativeQuery = true)
	public void saveOrganizerDataPermission(@Param("organizerId")Long organizerId, @Param("permissionId")Long permissionId);


	@Query(value = "SELECT tb.permission_id FROM sys_organizer_data_permission tb "
			+ "WHERE tb.organizer_id = :organizerId", nativeQuery = true)
    public List<Object> getPermissionIdByOrganizerId(@Param("organizerId")Long organizerId);  
	
	/**
	 * 删除DataPermissionRecur
	 * @param permissionChildId
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM sys_data_permission_recur "
			+ "WHERE PERMISSION_CHILD_ID = :permissionChildId ", nativeQuery = true)
	public void delDataPermissionRecurByChildId(@Param("permissionChildId")Long permissionChildId);
	
	/**
	 * 保存DataPermissionRecur
	 * @param permissionId
	 * @param permissionChildId
	 */
	@Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_data_permission_recur (PERMISSION_ID, PERMISSION_CHILD_ID) VALUES (:permissionId, :permissionChildId) ", nativeQuery = true)
	public void saveDataPermissionRecur(@Param("permissionId")Long permissionId, @Param("permissionChildId")Long permissionChildId);
	
}
