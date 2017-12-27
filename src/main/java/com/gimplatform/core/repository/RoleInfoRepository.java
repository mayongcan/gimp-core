package com.gimplatform.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.RoleInfo;
import com.gimplatform.core.repository.custom.RoleInfoRepositoryCustom;

/**
 * 用户信息资源操作类
 * @author zzd
 *
 */
@Repository
public interface RoleInfoRepository extends JpaRepository<RoleInfo, Long>, JpaSpecificationExecutor<RoleInfo>, RoleInfoRepositoryCustom{
	
	public List<RoleInfo> findByRoleNameAndTenantsIdAndOrganizerIdAndIsValid(String roleName, Long tenantsId, Long organizerId, String isValid);
	
	public List<RoleInfo> findByRoleNameAndTenantsIdAndIsValid(String roleName, Long tenantsId, String isValid);
	
//	@Query(value = "SELECT r.role_name FROM sys_user_role ur inner join sys_user_info u on ur.user_id = u.user_id inner join sys_role_info r on ur.role_id = r.role_id "
//			+ "WHERE u.user_code =:userCode", nativeQuery = true)
//    public List<String> getRolesNameByUserCode(@Param("userCode")String userCode);  
//
//	@Query(value = "SELECT r.role_name FROM sys_organizer_role tb inner join sys_role_info r on tb.role_id = r.role_id "
//			+ "WHERE tb.organizer_id =:organizerId", nativeQuery = true)
//    public List<String> getRolesNameByOrganizerId(@Param("organizerId")Long organizerId); 

	@Query(value = "SELECT r.role_name FROM sys_user_role ur inner join sys_user_info u on ur.user_id = u.user_id inner join sys_role_info r on ur.role_id = r.role_id "
			+ "WHERE u.user_code =:userCode "
			+ "union all "
			+ "SELECT r.role_name FROM sys_organizer_role tb inner join sys_role_info r on tb.role_id = r.role_id "
			+ "WHERE tb.organizer_id =:organizerId", nativeQuery = true)
	public List<String> getUserRoleName(@Param("userCode")String userCode, @Param("organizerId")Long organizerId);

	/**
	 * 删除RoleFunc关联表
	 * @param roleId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_role_func WHERE ROLE_ID =:roleId", nativeQuery = true)
	public void delRoleFuncByRoleId(@Param("roleId")Long roleId);

	/**
	 * 删除RoleFunc关联表
	 * @param funcId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_role_func WHERE FUNC_ID =:funcId", nativeQuery = true)
	public void delRoleFuncByFuncId(@Param("funcId")Long funcId);

	/**
	 * 保存RoleFunc
	 * @param roleId
	 * @param funcId
	 */
	@Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_role_func (ROLE_ID, FUNC_ID) VALUES (:roleId, :funcId) ", nativeQuery = true)
	public void saveRoleFunc(@Param("roleId")Long roleId, @Param("funcId")Long funcId);

	/**
	 * 删除UserRole关联表
	 * @param roleId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_user_role WHERE ROLE_ID =:roleId", nativeQuery = true)
	public void delUserRoleByRoleId(@Param("roleId")Long roleId);

	/**
	 * 删除UserRole关联表
	 * @param roleId
	 * @param idList
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_user_role WHERE ROLE_ID =:roleId AND USER_ID IN(:idList) ", nativeQuery = true)
	public void delUserRoleByRoleIdAndUserId(@Param("roleId")Long roleId, @Param("idList")List<Long> idList);

	/**
	 * 删除UserRole关联表
	 * @param roleId
	 * @param idList
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_user_role WHERE ROLE_ID =:roleId AND USER_ID =:userId ", nativeQuery = true)
	public void delUserRole(@Param("userId")Long userId, @Param("roleId")Long roleId);

	/**
	 * 保存UserRole
	 * @param userId
	 * @param roleId
	 */
	@Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_user_role (USER_ID, ROLE_ID) VALUES (:userId, :roleId) ", nativeQuery = true)
	public void saveUserRole(@Param("userId")Long userId, @Param("roleId")Long roleId);

	/**
	 * 删除OrganizerRole关联表
	 * @param roleId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_organizer_role WHERE ORGANIZER_ID =:organizerId", nativeQuery = true)
	public void delOrganizerRoleByOrganizerId(@Param("organizerId")Long organizerId);

	/**
	 * 删除OrganizerRole关联表
	 * @param roleId
	 * @param idList
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_organizer_role WHERE ROLE_ID =:roleId AND ORGANIZER_ID IN(:idList) ", nativeQuery = true)
	public void delOrganizerRoleByRoleIdAndOrganizerId(@Param("roleId")Long roleId, @Param("idList")List<Long> idList);

	/**
	 * 删除OrganizerRole关联表
	 * @param organizerId
	 * @param roleId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_organizer_role WHERE ROLE_ID =:roleId AND ORGANIZER_ID =:organizerId ", nativeQuery = true)
	public void delOrganizerRole(@Param("organizerId")Long organizerId, @Param("roleId")Long roleId);

	/**
	 * 保存OrganizerRole
	 * @param organizerId
	 * @param roleId
	 */
	@Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_organizer_role (ORGANIZER_ID, ROLE_ID) VALUES (:organizerId, :roleId) ", nativeQuery = true)
	public void saveOrganizerRole(@Param("organizerId")Long organizerId, @Param("roleId")Long roleId);
	

	@Query(value = "SELECT tb.role_id FROM sys_organizer_role tb "
			+ "WHERE tb.organizer_id = :organizerId", nativeQuery = true)
    public List<Object> getRoleIdByOrganizerId(@Param("organizerId")Long organizerId);  
}
