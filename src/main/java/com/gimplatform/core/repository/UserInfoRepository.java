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
import com.gimplatform.core.entity.UserInfo;
import com.gimplatform.core.repository.custom.UserInfoRepositoryCustom;

/**
 * 用户信息资源操作类
 * 
 * @author zzd
 *
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long>, UserInfoRepositoryCustom, JpaSpecificationExecutor<UserInfo> {
    
    List<UserInfo> findByOpenId(String openId);

	/**
	 * 根据用户Code查找用户信息
	 * 
	 * @param userCode
	 * @return
	 */
	public List<UserInfo> findByUserCode(String userCode);

	/**
	 * 根据用户Code isValid查找用户信息
	 * 
	 * @param userCode
	 * @param isValid
	 * @return
	 */
	public List<UserInfo> findByUserCodeAndIsValid(String userCode, String isValid);

	/**
	 * 根据organizerId,isValid查找组织
	 * 
	 * @param organizerId
	 * @param isValid
	 * @return
	 */
	public List<UserInfo> findByOrganizerIdAndIsValid(Long organizerId, String isValid);

	/**
	 * 通过组织ID和isValid查找用户数量
	 * 
	 * @param organizerId
	 * @param isValid
	 * @return
	 */
	@Query(value = "SELECT count(1) FROM sys_user_info WHERE ORGANIZER_ID =:organizerId AND IS_VALID =:isValid ", nativeQuery = true)
	public int getCountByOrganizerId(@Param("organizerId") Long organizerId, @Param("isValid") String isValid);

	/**
	 * 删除用户信息（将租户信息的IS_VALID设置为N）
	 * 
	 * @param isValid
	 * @param userId
	 * @param date
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_info " + "SET IS_VALID=:isValid, MODIFY_BY=:userId, MODIFY_DATE=:date "
			+ "WHERE USER_ID IN (:idList)", nativeQuery = true)
	public void delUser(@Param("isValid") String isValid, @Param("userId") Long userId, @Param("date") Date date,
			@Param("idList") List<Long> idList);

	/**
	 * 根据手机号码修改密码
	 * 
	 * @param password
	 * @param phone
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_info SET PASSWORD=:password WHERE MOBILE =:phone", nativeQuery = true)
	public void updatePasswordByMobile(@Param("password") String password, @Param("phone") String phone);

	/**
	 * 根据手机号码修改密码
	 * 
	 * @param password
	 * @param phone
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_info SET PASSWORD=:password WHERE USER_CODE =:userCode AND MOBILE =:mobile", nativeQuery = true)
	public void updatePasswordByUserCodeAndMobile(@Param("password") String password,
			@Param("userCode") String userCode, @Param("mobile") String mobile);

	/**
	 * 根据用户标识修改密码
	 * 
	 * @param password
	 * @param phone
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_info SET PASSWORD=:password WHERE user_id =:userId", nativeQuery = true)
	public void updatePasswordByUserId(@Param("password") String password, @Param("userId") Long userId);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_info SET PAY_PASSWORD = :payPassword WHERE user_id = :userId", nativeQuery = true)
	public void updatePayPassword(@Param("payPassword") String payPassword, @Param("userId") Long userId);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_info SET SAFETY_PASSWORD = :safetyPassword WHERE user_id = :userId", nativeQuery = true)
	public void updateSafetyPassword(@Param("safetyPassword") String safetyPassword, @Param("userId") Long userId);

	/**
	 * 删除信息（将信息的IS_VALID设置为N）
	 * 
	 * @param isValid
	 * @param userId
	 * @param date
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_info " + "SET IS_VALID=:isValid "
			+ "WHERE USER_ID IN (:idList)", nativeQuery = true)
	public void dimission(@Param("isValid") String isValid, @Param("idList") List<Long> idList);

	/**
	 * 保存OrgainzerPost
	 * 
	 * @param superiorUserId
	 * @param subordinateUserId
	 */
	@Transactional
	@Modifying
	@Query(value = "INSERT INTO sys_organizer_post (SUPERIOR_USER_ID, SUBORDINATE_USER_ID) VALUES (:superiorUserId, :subordinateUserId) ", nativeQuery = true)
	public void saveOrgainzerPost(@Param("superiorUserId") Long superiorUserId,
			@Param("subordinateUserId") Long subordinateUserId);

	/**
	 * 删除OrgainzerPost关联表
	 * 
	 * @param superiorUserId
	 */
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM sys_organizer_post WHERE SUPERIOR_USER_ID =:superiorUserId ", nativeQuery = true)
	public void delOrgainzerPost(@Param("superiorUserId") Long superiorUserId);

	/**
	 * 删除OrgainzerPost关联表
	 * 
	 * @param subordinateUserId
	 */
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM sys_organizer_post WHERE SUBORDINATE_USER_ID =:subordinateUserId ", nativeQuery = true)
	public void delOrgainzerPostBySubordinateUserId(@Param("subordinateUserId") Long subordinateUserId);

	/**
	 * 删除OrgainzerPost关联表
	 * 
	 * @param superiorUserId
	 * @param idList
	 */
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM sys_organizer_post WHERE SUPERIOR_USER_ID =:superiorUserId AND SUBORDINATE_USER_ID IN(:idList) ", nativeQuery = true)
	public void delOrgainzerPostBySuperiorUserId(@Param("superiorUserId") Long superiorUserId,
			@Param("idList") List<Long> idList);

	/**
	 * 获取用户下级
	 * 
	 * @param userId
	 * @return
	 */
	@Query(value = "SELECT SUBORDINATE_USER_ID FROM sys_organizer_post where SUPERIOR_USER_ID = :userId ", nativeQuery = true)
	public List<Long> getSubordinateList(@Param("userId") Long userId);

	/**
	 * 获取用户直属上级
	 * 
	 * @param userCode
	 * @return
	 */
	@Query(value = "SELECT su.user_code FROM sys_organizer_post o left join sys_user_info u on o.SUBORDINATE_USER_ID = u.user_id "
			+ "left join sys_user_info su on su.user_id = o.SUPERIOR_USER_ID "
			+ "where u.user_code = :userCode ", nativeQuery = true)
	public List<String> getSuperiorByUserCode(@Param("userCode") String userCode);

	/**
	 * 更新beginDate
	 * 
	 * @param tenantsId
	 * @param beginDate
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_logon SET VALID_BEGIN_DATE=:beginDate "
			+ "WHERE user_id in (SELECT USER_ID FROM sys_user_info WHERE TENANTS_ID = :tenantsId)", nativeQuery = true)
	public void updateBeginDateByTenantsId(@Param("tenantsId") Long tenantsId, @Param("beginDate") Date beginDate);

	/**
	 * 更新beginDate
	 * 
	 * @param tenantsId
	 * @param endDate
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_logon SET VALID_END_DATE=:endDate "
			+ "WHERE user_id in (SELECT USER_ID FROM sys_user_info WHERE TENANTS_ID = :tenantsId)", nativeQuery = true)
	public void updateEndDateByTenantsId(@Param("tenantsId") Long tenantsId, @Param("endDate") Date endDate);

	/**
	 * 根据用户标识修改openid
	 * 
	 * @param openid
	 * @param userId
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_info SET OPEN_ID=:openId WHERE user_id =:userId", nativeQuery = true)
	public void updateOpenIdByUserId(@Param("openId") String openId, @Param("userId") Long userId);
}
