package com.gimplatform.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gimplatform.core.entity.District;

/**
 * 区域资源操作类
 * @author zzd
 *
 */
@Repository
public interface DistrictRepository extends JpaRepository<District, Long>{

}
