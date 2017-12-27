package com.gimplatform.core.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.entity.scheduler.QrtzFiredDetails;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.QrtzFiredDetailsRepositoryCustom;
import com.gimplatform.core.utils.StringUtils;

@Transactional
public class QrtzFiredDetailsRepositoryImpl extends BaseRepository implements QrtzFiredDetailsRepositoryCustom{
	
	private static final String MYSQL_SQL_GET_LIST = "SELECT FIRED_ID, JOB_NAME, JOB_GROUP, TRIGGER_GROUP, DATE_FORMAT(START_DATE,'%Y-%m-%d %T') AS START_DATE,"
					+ "CASE WHEN SUBSTR(TRIGGER_NAME, 1, 3) = 'MT_' THEN '手动触发' ELSE TRIGGER_NAME END AS TRIGGER_NAME,  DATE_FORMAT(END_DATE,'%Y-%m-%d %T') AS END_DATE, "
					+ "DATE_FORMAT(FIRED_DATE,'%Y-%m-%d %T') AS FIRED_DATE, DATE_FORMAT(NEXT_FIRED_DATE,'%Y-%m-%d %T') AS NEXT_FIRED_DATE, JOB_TYPE, JOB_STATUS, TRIGGER_TYPE, FIRED_RESULT "
			+ "FROM qrtz_fired_details "
			+ "WHERE FIRED_ID IS NOT NULL " ;
	
	private static final String MYSQL_SQL_GET_LIST_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM qrtz_fired_details "
			+ "WHERE FIRED_ID IS NOT NULL " ;

	private static final String ORACLE_SQL_GET_LIST = "SELECT FIRED_ID, JOB_NAME, JOB_GROUP, TRIGGER_GROUP, TO_CHAR(START_DATE,'YYYY-MM-DD') AS START_DATE, "
					+ "CASE WHEN SUBSTR(TRIGGER_NAME, 1, 3) = 'MT_' THEN '手动触发' ELSE TRIGGER_NAME END TRIGGER_NAME, TO_CHAR(END_DATE,'YYYY-MM-DD') AS END_DATE, "
					+ "TO_CHAR(FIRED_DATE,'YYYY-MM-DD') AS FIRED_DATE, TO_CHAR(NEXT_FIRED_DATE,'YYYY-MM-DD') AS NEXT_FIRED_DATE, JOB_TYPE, JOB_STATUS, TRIGGER_TYPE, FIRED_RESULT "
			+ "FROM qrtz_fired_details "
			+ "WHERE FIRED_ID IS NOT NULL " ;
	
	private static final String ORACLE_SQL_GET_LIST_COUNT = "SELECT count(1) as \"count\" "
			+ "FROM qrtz_fired_details "
			+ "WHERE FIRED_ID IS NOT NULL " ;

	public QrtzFiredDetailsRepositoryImpl(){
		sqlMap = new HashMap<String, String>();
		sqlMap.put("MYSQL_SQL_GET_LIST", MYSQL_SQL_GET_LIST);
		sqlMap.put("MYSQL_SQL_GET_LIST_COUNT", MYSQL_SQL_GET_LIST_COUNT);
		sqlMap.put("ORACLE_SQL_GET_LIST", ORACLE_SQL_GET_LIST);
		sqlMap.put("ORACLE_SQL_GET_LIST_COUNT", ORACLE_SQL_GET_LIST_COUNT);
	}
	
	public List<Map<String, Object>> getList(QrtzFiredDetails qrtzFiredDetails, int pageIndex, int pageSize) {
		//生成查询条件
		SqlParams sqlParams = genListWhere(getSqlContent("SQL_GET_LIST"), qrtzFiredDetails);
		//添加分页和排序
		sqlParams = getPageableSql(sqlParams, pageIndex, pageSize, " FIRED_ID DESC ", " FIRED_ID DESC ");
		return getResultList(sqlParams);
	}

	public int getListCount(QrtzFiredDetails qrtzFiredDetails) {
		//生成查询条件
		SqlParams sqlParams = genListWhere(getSqlContent("SQL_GET_LIST_COUNT"), qrtzFiredDetails);
		return getResultListTotalCount(sqlParams);
	}
	
	/**
	 * 生成查询条件
	 * @param sql
	 * @param qrtzFiredDetails
	 * @return
	 */
	private SqlParams genListWhere(String sql, QrtzFiredDetails qrtzFiredDetails){
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(sql);
		//添加查询参数
		if(qrtzFiredDetails != null && !StringUtils.isBlank(qrtzFiredDetails.getJobName())) {
			sqlParams.querySql.append(" AND JOB_NAME = :jobName ");
			sqlParams.paramsList.add("jobName");
			sqlParams.valueList.add(qrtzFiredDetails.getJobName());
        }
		if(qrtzFiredDetails != null && !StringUtils.isBlank(qrtzFiredDetails.getJobGroup())) {
			sqlParams.querySql.append(" AND AND JOB_GROUP = :jobGroup ");
			sqlParams.paramsList.add("jobGroup");
			sqlParams.valueList.add(qrtzFiredDetails.getJobGroup());
		}
		if(qrtzFiredDetails != null && qrtzFiredDetails.getStartDate() != null && qrtzFiredDetails.getEndDate() != null) {
			sqlParams.querySql.append(" AND START_DATE BETWEEN :startDate AND :endDate ");
			sqlParams.paramsList.add("startDate");
			sqlParams.paramsList.add("endDate");
			sqlParams.valueList.add(qrtzFiredDetails.getStartDate());
			sqlParams.valueList.add(qrtzFiredDetails.getEndDate());
		}
        return sqlParams;
	}

}
