package com.gimplatform.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.UserLogon;
import com.gimplatform.core.repository.custom.UserLogonRepositoryCustom;

/**
 * 用户登录信息资源操作类
 * @author zzd
 *
 */
@Repository
public interface UserLogonRepository extends JpaRepository<UserLogon, Long>, UserLogonRepositoryCustom{

	/**
	 * 根据用户Code查找用户信息
	 * 
	 * @param userCode
	 * @return
	 */
	public UserLogon findByUserId(Long userId);
	
	/**
	 * 解锁用户
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_user_logon SET FAILE_COUNT = 0, LOCK_BEGIN_DATE = NULL, LOCK_END_DATE=NULL, LOCK_REASON= '' "
			+ "WHERE USER_ID IN (:idList)", nativeQuery = true)
	public void unlockAccount(@Param("idList") List<Long> idList);
}
