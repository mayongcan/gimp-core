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

import com.gimplatform.core.entity.DictType;

/**
 * 字典类型资源操作类
 * @author zzd
 *
 */
@Repository
public interface DictTypeRepository extends JpaRepository<DictType, Long>, JpaSpecificationExecutor<DictType>{

	/**
	 * 根据value和isValid查找字典类型
	 * @param value
	 * @param isValid
	 * @return
	 */
	public DictType findByValueAndIsValid(String value, String isValid);
	
	/**
	 * 删除字典信息（将字典信息的IS_VALID设置为N）
	 * @param isValid
	 * @param userId
	 * @param date
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_dict_type "
			+ "SET IS_VALID = :isValid, MODIFY_BY = :userId, MODIFY_DATE = :date "
			+ "WHERE DICT_TYPE_ID IN (:idList)", nativeQuery = true)
	public void delDictType(@Param("isValid")String isValid, @Param("userId")Long userId, @Param("date")Date date, @Param("idList")List<Long> idList);
}
