package com.gimplatform.core.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections4.MapUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.NoRepositoryBean;

import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.utils.StringUtils;

@NoRepositoryBean
public class BaseRepository {
	
	@Value("${spring.jpa.database-platform}")
	private String databasePlatform;
	
	protected static final int DATABASE_UNKNOW = 0;
	
	protected static final int DATABASE_MYSQL = 1;
	
	protected static final int DATABASE_ORACLE = 2;
	
	protected Map<String, String> sqlMap = new HashMap<String, String>();
	
	@PersistenceContext
    protected EntityManager entityManager;
	
	/**
	 * 获取数据类型
	 * @return
	 */
	protected int getDatabaseType(){
		if("org.hibernate.dialect.MySQL5InnoDBDialect".equals(databasePlatform)){
			return DATABASE_MYSQL;
		}else if("org.hibernate.dialect.Oracle10gDialect".equals(databasePlatform)){
			return DATABASE_ORACLE;
		}else{
			return DATABASE_UNKNOW;
		}
	}
	
	/**
	 * 根据名称获取SQL内容
	 * @param name
	 * @return
	 */
	protected String getSqlContent(String name){
		String sqlContent = "";
		int databaseType = getDatabaseType();
		if(databaseType == DATABASE_MYSQL){
			sqlContent = MapUtils.getString(sqlMap, "MYSQL_" + name);
		}else if(databaseType == DATABASE_ORACLE){
			sqlContent = MapUtils.getString(sqlMap, "ORACLE_" + name);
		}else{
			
		}
		return sqlContent;
	}
	
	/**
	 * 根据不同的数据库获取分页sql
	 * @param sqlParams
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	protected SqlParams getPageableSql(SqlParams sqlParams, int pageIndex, int pageSize){
		return getPageableSql(sqlParams, pageIndex, pageSize, null, null);
	}
	
	/**
	 * 根据不同的数据库获取分页sql
	 * @param sqlParams
	 * @param pageIndex
	 * @param pageSize
	 * @param mysqlSort
	 * @param oracleSort
	 * @return
	 */
	protected SqlParams getPageableSql(SqlParams sqlParams, int pageIndex, int pageSize, String mysqlSort, String oracleSort){
		int databaseType = getDatabaseType();
		if(databaseType == DATABASE_MYSQL){
			StringBuffer sb = new StringBuffer();
			sb.append(sqlParams.querySql);
			if(!StringUtils.isBlank(mysqlSort)){
				sb.append(" ORDER BY " + mysqlSort);
			}
			sb.append(" LIMIT :pageIndex , :pageSize ");
			sqlParams.paramsList.add("pageIndex");
			sqlParams.valueList.add(pageIndex * pageSize);
			sqlParams.paramsList.add("pageSize");
			sqlParams.valueList.add(pageSize);
			sqlParams.querySql = sb;
		}else if(databaseType == DATABASE_ORACLE){
			StringBuffer sb = new StringBuffer();
			if(!StringUtils.isBlank(oracleSort)){
				sb.append("SELECT * FROM(SELECT ROW_NUMBER() OVER ( ORDER BY ");
				sb.append(oracleSort);
				sb.append(" ) AS RowNumber, T.* FROM( ");
			}else{
				sb.append("SELECT * FROM(SELECT ROWNUM AS RowNumber, T.* FROM( ");
			}
			//添加主sql
			sb.append(sqlParams.querySql);
			//添加分页
			sb.append(" ) T ) T WHERE RowNumber BETWEEN :pageIndex AND :pageSize ");
			sqlParams.paramsList.add("pageIndex");
			sqlParams.valueList.add(pageIndex * pageSize + 1);
			sqlParams.paramsList.add("pageSize");
			sqlParams.valueList.add(pageIndex * pageSize + pageSize);
			sqlParams.querySql = sb;
		}else{
			
		}
		return sqlParams;
	}
	
	/**
	 * 获取查询返回的列表
	 * @param sqlParams
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> getResultList(SqlParams sqlParams){
		//创建查询对象
		Query query = entityManager.createNativeQuery(sqlParams.querySql.toString()); 
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		//设置查询参数
		for(int i = 0; i < sqlParams.paramsList.size(); i++){
			query.setParameter(sqlParams.paramsList.get(i), sqlParams.valueList.get(i));
		}
		return query.getResultList();
	}

	/**
	 * 获取查询返回的总条数
	 * @param sqlParams
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected int getResultListTotalCount(SqlParams sqlParams){
		//创建查询对象
		Query query = entityManager.createNativeQuery(sqlParams.querySql.toString()); 
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		//设置查询参数
		for(int i = 0; i < sqlParams.paramsList.size(); i++){
			query.setParameter(sqlParams.paramsList.get(i), sqlParams.valueList.get(i));
		}
		List resultList = query.getResultList();
		if(resultList.size() == 0 ){
			return 0;
		}else{
			//Map<String, Object> map = (Map<String, Object>) query.getSingleResult();
			Map<String, Object> map = (Map<String, Object>) resultList.get(0);
			return MapUtils.getInteger(map, "count", 0);
		}
	}


	/**
	 * 获取查询返回的总条数
	 * @param sqlParams
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected int getResultListUnionAllTotalCount(SqlParams sqlParams){
		// 创建查询对象
		Query query = entityManager.createNativeQuery(sqlParams.querySql.toString()); 
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		// 设置查询参数
		for(int i = 0; i < sqlParams.paramsList.size(); i++){
			query.setParameter(sqlParams.paramsList.get(i), sqlParams.valueList.get(i));
		}
		List<Map<String, Object>> list = query.getResultList();
		int count = 0;
		for(Map<String, Object> map : list){
			count += MapUtils.getInteger(map, "count", 0);
		}
		return count;
	}
}
