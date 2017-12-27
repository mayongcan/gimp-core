package com.gimplatform.core.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.FuncInfo;
import com.gimplatform.core.repository.custom.FuncInfoRepositoryCustom;


/**
 * 字典信息资源操作类
 * @author zzd
 *
 */
@Repository
public interface FuncInfoRepository extends JpaRepository<FuncInfo, Long>, FuncInfoRepositoryCustom{
	
	/**
	 * 根据funcFlag和isValid查找权限
	 * @param funcFlag
	 * @param isValid
	 * @return
	 */
	public List<FuncInfo> findByFuncFlagAndIsValid(String funcFlag, String isValid);

	
//	@Query(value = "SELECT DISTINCT(func.FUNC_ID), func.* "
//			+ "FROM sys_user_role ur LEFT JOIN sys_role_func rf ON ur.ROLE_ID=rf.ROLE_ID LEFT JOIN sys_func_info func ON rf.FUNC_ID=func.FUNC_ID "
//			+ "WHERE func.IS_VALID='Y' AND func.IS_SHOW = 'Y' AND rf.FUNC_ID IS NOT NULL AND ur.USER_ID = :userId "
//			+ "ORDER BY func.DISP_ORDER ASC, func.FUNC_ID ASC", nativeQuery = true)
//    public List<FuncInfo> getUserFunc(@Param("userId")Long userId);  
//
//	//获取用户继承组织的权限
//	@Query(value = "SELECT DISTINCT(func.FUNC_ID), func.* "
//			+ "FROM sys_organizer_role tb LEFT JOIN sys_role_func rf ON tb.ROLE_ID=rf.ROLE_ID LEFT JOIN sys_func_info func ON rf.FUNC_ID=func.FUNC_ID "
//			+ "WHERE func.IS_VALID='Y' AND func.IS_SHOW = 'Y' AND rf.FUNC_ID IS NOT NULL AND tb.ORGANIZER_ID = :organizerId "
//			+ "ORDER BY func.DISP_ORDER ASC, func.FUNC_ID ASC", nativeQuery = true)
//    public List<FuncInfo> getUserOrganizerFunc(@Param("organizerId")Long organizerId);  
	
//	@Query(value = "SELECT f.* "
//			+ "FROM sys_func_info f "
//			+ "WHERE f.func_id IN (SELECT DISTINCT func.func_id "
//					+ "FROM sys_user_role ur INNER JOIN sys_role_info r ON ur.role_id = r.role_id INNER JOIN sys_role_func rf ON r.role_id = rf.role_id "
//					+ "INNER JOIN sys_func_info func ON rf.func_id = func.func_id  INNER JOIN sys_tenants_func tf on rf.func_id = tf.func_id "
//					+ "WHERE ur.user_id = :userId and tf.tenants_id = :tenantsId and func.PARENT_FUNC_ID is not null AND func.is_show = 'Y' AND func.is_valid = 'Y' AND r.is_valid = 'Y' "
//					+ "AND func.PARENT_FUNC_ID = (SELECT FUNC_ID FROM SYS_FUNC_INFO WHERE PARENT_FUNC_ID IS NULL )) "
//			+ "AND f.func_level = 1 "
//			+ "ORDER BY f.disp_order, f.FUNC_NAME", nativeQuery = true)
//    public List<FuncInfo> getUserFuncFolder(@Param("userId")Long userId, @Param("tenantsId")Long tenantsId);  
//
//	
//	@Query(value = "SELECT DISTINCT(F.FUNC_ID), F.*  "
//			+ "FROM sys_func_info F INNER JOIN sys_role_func R ON F.FUNC_ID=R.FUNC_ID INNER JOIN sys_user_role U ON R.ROLE_ID=U.ROLE_ID "
//			+ "WHERE F.IS_VALID='Y' AND (F.FUNC_TYPE=100200 OR F.FUNC_TYPE=100300) AND F.IS_SHOW='Y' AND F.PARENT_FUNC_ID=:folderId AND U.USER_ID = :userId " //支持目录查询
//			+ "ORDER BY F.DISP_ORDER, F.FUNC_ID", nativeQuery = true)
//    public List<FuncInfo> getUserFuncListByFd(@Param("userId")Long userId, @Param("folderId")Long folderId);  
//
//	@Query(value = "SELECT f.* "
//			+ "FROM sys_func_info f "
//			+ "WHERE f.func_id IN (SELECT DISTINCT func.func_id "
//					+ "FROM sys_organizer_role tb INNER JOIN sys_role_info r ON tb.role_id = r.role_id INNER JOIN sys_role_func rf ON r.role_id = rf.role_id "
//					+ "INNER JOIN sys_func_info func ON rf.func_id = func.func_id INNER JOIN sys_tenants_func tf on rf.func_id = tf.func_id "
//					+ "WHERE tb.organizer_id = :organizerId and tf.tenants_id = :tenantsId and func.PARENT_FUNC_ID is not null AND func.is_show = 'Y' AND func.is_valid = 'Y' AND r.is_valid = 'Y' "
//					+ "AND func.PARENT_FUNC_ID = (SELECT FUNC_ID FROM SYS_FUNC_INFO WHERE PARENT_FUNC_ID IS NULL )) "
//			+ "AND f.func_level = 1 "
//			+ "ORDER BY f.disp_order, f.FUNC_NAME", nativeQuery = true)
//    public List<FuncInfo> getOrganizerFuncFolder(@Param("organizerId")Long organizerId, @Param("tenantsId")Long tenantsId);  
//
//	
//	@Query(value = "SELECT DISTINCT(F.FUNC_ID), F.*  "
//			+ "FROM sys_func_info F INNER JOIN sys_role_func R ON F.FUNC_ID=R.FUNC_ID INNER JOIN sys_organizer_role tb ON R.ROLE_ID=tb.ROLE_ID "
//			+ "WHERE F.IS_VALID='Y' AND (F.FUNC_TYPE=100200 OR F.FUNC_TYPE=100300) AND F.IS_SHOW='Y' AND F.PARENT_FUNC_ID=:folderId AND tb.organizer_id = :organizerId " //支持目录查询
//			+ "ORDER BY F.DISP_ORDER, F.FUNC_ID", nativeQuery = true)
//    public List<FuncInfo> getOrganizerFuncListByFd(@Param("organizerId")Long organizerId, @Param("folderId")Long folderId);
	

	@Query(value = "SELECT DISTINCT(tmp.FUNC_ID), tmp.* FROM( "
			+ "SELECT func.* "
			+ "FROM sys_user_role ur LEFT JOIN sys_role_func rf ON ur.ROLE_ID=rf.ROLE_ID LEFT JOIN sys_func_info func ON rf.FUNC_ID=func.FUNC_ID "
			+ "WHERE func.IS_VALID='Y' AND func.IS_SHOW = 'Y' AND rf.FUNC_ID IS NOT NULL AND ur.USER_ID = :userId "
				+ "UNION ALL "
			+ "SELECT func.* "
			+ "FROM sys_organizer_role tb LEFT JOIN sys_role_func rf ON tb.ROLE_ID=rf.ROLE_ID LEFT JOIN sys_func_info func ON rf.FUNC_ID=func.FUNC_ID "
			+ "WHERE func.IS_VALID='Y' AND func.IS_SHOW = 'Y' AND rf.FUNC_ID IS NOT NULL AND tb.ORGANIZER_ID = :organizerId "
			+ ") tmp ORDER BY tmp.DISP_ORDER ASC, tmp.FUNC_ID ASC", nativeQuery = true)
    public List<FuncInfo> getUserFunc(@Param("userId")Long userId, @Param("organizerId")Long organizerId); 

	@Query(value = "SELECT DISTINCT(tmp.FUNC_ID), tmp.* FROM( "
			+ "SELECT f.* "
			+ "FROM sys_func_info f "
			+ "WHERE f.func_id IN (SELECT DISTINCT func.func_id "
					+ "FROM sys_user_role ur INNER JOIN sys_role_info r ON ur.role_id = r.role_id INNER JOIN sys_role_func rf ON r.role_id = rf.role_id "
					+ "INNER JOIN sys_func_info func ON rf.func_id = func.func_id  INNER JOIN sys_tenants_func tf on rf.func_id = tf.func_id "
					+ "WHERE ur.user_id = :userId and tf.tenants_id = :tenantsId and func.PARENT_FUNC_ID is not null AND func.is_show = 'Y' AND func.is_valid = 'Y' AND r.is_valid = 'Y' "
					+ "AND func.PARENT_FUNC_ID = (SELECT FUNC_ID FROM SYS_FUNC_INFO WHERE PARENT_FUNC_ID IS NULL )) "
			+ "AND f.func_level = 1 "
				+ "UNION ALL "
			+ "SELECT f.* "
			+ "FROM sys_func_info f "
			+ "WHERE f.func_id IN (SELECT DISTINCT func.func_id "
					+ "FROM sys_organizer_role tb INNER JOIN sys_role_info r ON tb.role_id = r.role_id INNER JOIN sys_role_func rf ON r.role_id = rf.role_id "
					+ "INNER JOIN sys_func_info func ON rf.func_id = func.func_id INNER JOIN sys_tenants_func tf on rf.func_id = tf.func_id "
					+ "WHERE tb.organizer_id = :organizerId and tf.tenants_id = :tenantsId and func.PARENT_FUNC_ID is not null AND func.is_show = 'Y' AND func.is_valid = 'Y' AND r.is_valid = 'Y' "
					+ "AND func.PARENT_FUNC_ID = (SELECT FUNC_ID FROM SYS_FUNC_INFO WHERE PARENT_FUNC_ID IS NULL )) "
			+ "AND f.func_level = 1 "
			+ ") tmp ORDER BY tmp.disp_order, tmp.FUNC_NAME", nativeQuery = true)
    public List<FuncInfo> getUserFuncFolder(@Param("userId")Long userId, @Param("organizerId")Long organizerId, @Param("tenantsId")Long tenantsId);  

	@Query(value = "SELECT DISTINCT(tmp.FUNC_ID), tmp.* FROM( "
			+ "SELECT F.*  "
			+ "FROM sys_func_info F INNER JOIN sys_role_func R ON F.FUNC_ID=R.FUNC_ID INNER JOIN sys_user_role U ON R.ROLE_ID=U.ROLE_ID "
			+ "WHERE F.IS_VALID='Y' AND (F.FUNC_TYPE=100200 OR F.FUNC_TYPE=100300) AND F.IS_SHOW='Y' AND F.PARENT_FUNC_ID=:folderId AND U.USER_ID = :userId " //支持目录查询
				+ "UNION ALL "
			+ "SELECT F.*  "
			+ "FROM sys_func_info F INNER JOIN sys_role_func R ON F.FUNC_ID=R.FUNC_ID INNER JOIN sys_organizer_role tb ON R.ROLE_ID=tb.ROLE_ID "
			+ "WHERE F.IS_VALID='Y' AND (F.FUNC_TYPE=100200 OR F.FUNC_TYPE=100300) AND F.IS_SHOW='Y' AND F.PARENT_FUNC_ID=:folderId AND tb.organizer_id = :organizerId " //支持目录查询
			+ ") tmp ORDER BY tmp.DISP_ORDER, tmp.FUNC_ID", nativeQuery = true)
    public List<FuncInfo> getUserFuncListByFd(@Param("userId")Long userId, @Param("organizerId")Long organizerId, @Param("folderId")Long folderId); 

	/**
	 * 根据租户ID获取权限列表
	 * @param tenantsId
	 * @return
	 */
	@Query(value = "SELECT func.FUNC_ID "
			+ "FROM sys_tenants_func tf INNER JOIN sys_func_info func ON tf.FUNC_ID= func.FUNC_ID AND func.IS_VALID='Y' "
			+ "WHERE tf.TENANTS_ID =:tenantsId "
			+ "ORDER BY func.PARENT_FUNC_ID, func.DISP_ORDER, func.FUNC_ID", nativeQuery = true)
	public List<Object> getFuncIdByTenantsId(@Param("tenantsId")Long tenantsId);

	/**
	 * 根据角色ID返回权限树
	 * @param roleId
	 * @return
	 */
	@Query(value = "SELECT FUNC_ID as funcId FROM SYS_FUNC_INFO WHERE FUNC_ID IN("
			+ "SELECT FUNC_ID FROM SYS_ROLE_FUNC WHERE ROLE_ID=:roleId) "
			+ "AND IS_VALID='Y'", nativeQuery = true)
    public List<Object> getFuncTreeByRoleId(@Param("roleId")Long roleId); 

	/**
	 * 根据父ID列表获取权限ID列表
	 * @param idList
	 * @return
	 */
	@Query(value = "SELECT * FROM sys_func_info WHERE IS_VALID='Y' AND PARENT_FUNC_ID IN (:idList)", nativeQuery = true)
    public List<FuncInfo> getFuncByParentIds(@Param("idList")List<Long> idList);  
	
	/**
	 * 删除权限信息（将租户信息的IS_VALID设置为N）
	 * @param isValid
	 * @param userId
	 * @param date
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_func_info "
			+ "SET IS_VALID=:isValid, MODIFY_BY=:userId, MODIFY_DATE=:date "
			+ "WHERE FUNC_ID IN (:idList)", nativeQuery = true)
	public void delFunc(@Param("isValid")String isValid, @Param("userId")Long userId, @Param("date")Date date, @Param("idList")List<Long> idList);
}
