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

import com.gimplatform.core.entity.DictData;
import com.gimplatform.core.repository.custom.DictDataRepositoryCustom;

/**
 * 字典信息资源操作类
 * @author zzd
 *
 */
@Repository
public interface DictDataRepository extends JpaRepository<DictData, Long>, JpaSpecificationExecutor<DictData>, DictDataRepositoryCustom{

	/**
	 * 获取字典数据
	 * @param isValid
	 * @param value
	 * @param dictTypeId
	 * @param tenantsId
	 * @return
	 */
	@Query(value = "SELECT * FROM sys_dict_data "
			+ "WHERE VALUE=:value AND IS_VALID=:isValid AND DICT_TYPE_ID=:dictTypeId AND (TENANTS_ID=:tenantsId OR TENANTS_ID = -1) ", nativeQuery = true)
	public DictData getDictDataByParams(@Param("isValid")String isValid, @Param("value")String value, @Param("dictTypeId")Long dictTypeId, @Param("tenantsId")Long tenantsId);
	

	/**
	 * 删除字典信息（将字典信息的IS_VALID设置为N）
	 * @param isValid
	 * @param userId
	 * @param date
	 * @param idList
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_dict_data "
			+ "SET IS_VALID = :isValid, MODIFY_BY = :userId, MODIFY_DATE = :date "
			+ "WHERE DICT_DATA_ID IN (:idList)", nativeQuery = true)
	public void delDictData(@Param("isValid")String isValid, @Param("userId")Long userId, @Param("date")Date date, @Param("idList")List<Long> idList);
	

    /**
     * 更新排序ID
     * @param dictDataId
     * @param dispOrder
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE sys_dict_data "
            + "SET DISP_ORDER = :dispOrder "
            + "WHERE DICT_DATA_ID = :dictDataId ", nativeQuery = true)
    public void updateDispOrderById(@Param("dictDataId")Long dictDataId, @Param("dispOrder")Long dispOrder);
}
