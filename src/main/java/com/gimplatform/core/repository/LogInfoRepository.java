package com.gimplatform.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gimplatform.core.entity.LogInfo;
import com.gimplatform.core.repository.custom.LogInfoRepositoryCustom;

/**
 * 日志信息资源操作类
 * @author zzd
 *
 */
@Repository
public interface LogInfoRepository extends JpaRepository<LogInfo, Long>, LogInfoRepositoryCustom{

}
