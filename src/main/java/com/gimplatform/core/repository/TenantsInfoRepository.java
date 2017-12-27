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

import com.gimplatform.core.entity.TenantsInfo;

/**
 * 租户信息资源操作类
 * @author zzd
 *
 */
@Repository
public interface TenantsInfoRepository extends JpaRepository<TenantsInfo, Long>, JpaSpecificationExecutor<TenantsInfo> {

	/**
	 * 根据isValid查找租户
	 * @param isValid
	 * @return
	 */
	public List<TenantsInfo> findByIsValid(String isValid);
	
	/**
	 * 根据tenantsName和isValid查找租户
	 * @param tenantsName
	 * @param isValid
	 * @return
	 */
	public TenantsInfo findByTenantsNameAndIsValid(String tenantsName, String isValid);

	/**
	 * 根据tenantsId和isRoot查找租户
	 * @param tenantsId
	 * @param isRoot
	 * @return
	 */
	public List<TenantsInfo> findByTenantsIdAndIsRoot(Long tenantsId, String isRoot);
	
	/**
	 * 删除租户信息（将租户信息的IS_VALID设置为N）
	 * @param isValid
	 * @param userId
	 * @param date
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_tenants_info "
			+ "SET IS_VALID=:isValid, MODIFY_BY=:userId, MODIFY_DATE=:date "
			+ "WHERE TENANTS_ID IN (:idList)", nativeQuery = true)
	public void delTenants(@Param("isValid")String isValid, @Param("userId")Long userId, @Param("date")Date date, @Param("idList")List<Long> idList);
	 

	/**
	 * 删除TenantsFunc关联表
	 * @param tenantsId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_tenants_func WHERE TENANTS_ID =:tenantsId", nativeQuery = true)
	public void delTenantsFuncByTenantsId(@Param("tenantsId")Long tenantsId);

	/**
	 * 删除TenantsFunc关联表
	 * @param funcId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_tenants_func WHERE FUNC_ID =:funcId", nativeQuery = true)
	public void delTenantsFuncByFuncId(@Param("funcId")Long funcId);
	
	/**
	 * 保存TenantsFunc
	 * @param tenantsId
	 * @param funcId
	 */
	@Transactional
    @Modifying
    @Query(value = "INSERT INTO sys_tenants_func (TENANTS_ID, FUNC_ID) VALUES (:tenantsId, :funcId) ", nativeQuery = true)
	public void saveTenantsFunc(@Param("tenantsId")Long tenantsId, @Param("funcId")Long funcId);
}
