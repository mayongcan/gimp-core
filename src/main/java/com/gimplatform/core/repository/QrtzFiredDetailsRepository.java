package com.gimplatform.core.repository;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.scheduler.QrtzFiredDetails;
import com.gimplatform.core.repository.custom.QrtzFiredDetailsRepositoryCustom;

@Repository
public interface QrtzFiredDetailsRepository extends JpaRepository<QrtzFiredDetails, Long>, JpaSpecificationExecutor<QrtzFiredDetails>, QrtzFiredDetailsRepositoryCustom{

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE qrtz_fired_details "
			+ "SET END_DATE=:endDate, FIRED_RESULT=:firedResult, JOB_STATUS=:jobStatus "
			+ "WHERE JOB_NAME=:jobName and JOB_GROUP=:jobGroup and TRIGGER_NAME=:tgName and TRIGGER_GROUP=:tgGroup and JOB_STATUS=:jobStatusWhere and FIRED_DATE=:fireDate", nativeQuery = true)
	public void updateDetails(@Param("endDate")Date endDate, @Param("firedResult")String firedResult, @Param("jobStatus")String jobStatus, 
			@Param("jobName")String jobName, @Param("jobGroup")String jobGroup, @Param("tgName")String tgName, @Param("tgGroup")String tgGroup, @Param("jobStatusWhere")String jobStatusWhere, @Param("fireDate")Date fireDate);

}
