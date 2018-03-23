package com.gimplatform.core.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.FuncInfoRepositoryCustom;
import com.gimplatform.core.utils.StringUtils;

/**
 * 字典信息资源操作类
 * @author zzd
 *
 */
@Transactional
public class FuncInfoRepositoryImpl extends BaseRepository implements FuncInfoRepositoryCustom{

	//用户权限子菜单
	private static final String SQL_GET_ALL_FUNC_DATA = "SELECT F.FUNC_ID as \"funcId\", F.PARENT_FUNC_ID as \"parentFuncId\", F.FUNC_NAME as \"funcName\", F.IS_BASE as \"isBase\", "
					+ "F.FUNC_TYPE as \"funcType\", F.FUNC_LEVEL as \"funcLevel\", F.FUNC_LINK as \"funcLink\", F.DISP_POSITION as \"dispPosition\", F.FUNC_DESC as \"funcDesc\" "
			+ "FROM SYS_FUNC_INFO F "
			+ "WHERE F.IS_VALID='Y'";
	
	private static final String SQL_GET_FUNC_TREE_LIST = "SELECT FUNC_ID as \"funcId\", FUNC_NAME as \"funcName\", PARENT_FUNC_ID as \"parentFuncId\", DISP_ORDER as \"dispOrder\", "
					+ "FUNC_TYPE as \"funcType\", FUNC_LEVEL as \"funcLevel\", FUNC_LINK as \"funcLink\", FUNC_FLAG as \"funcFlag\", IS_SHOW as \"isShow\", "
					+ "IS_BLANK as \"isBlank\", FUNC_ICON as \"funcIcon\", DISP_POSITION as \"dispPosition\", FUNC_DESC as \"funcDesc\", IS_BASE as \"isBase\" "
			+ "FROM SYS_FUNC_INFO WHERE IS_VALID = 'Y'";
	
	private static final String SQL_GET_FUNC_BY_TENANTSID = "SELECT func.FUNC_ID as \"funcId\", func.FUNC_NAME as \"funcName\", func.PARENT_FUNC_ID as \"parentFuncId\", "
					+ "func.DISP_ORDER as \"dispOrder\", func.FUNC_TYPE as \"funcType\", func.FUNC_LEVEL as \"funcLevel\", func.FUNC_LINK as \"funcLink\", func.FUNC_FLAG as \"funcFlag\", "
					+ "func.IS_SHOW as \"isShow\", func.IS_BLANK as \"isBlank\", func.FUNC_ICON as \"funcIcon\", func.DISP_POSITION as \"dispPosition\", func.FUNC_DESC as \"funcDesc\" , func.IS_BASE as \"isBase\" "
			+ "FROM sys_tenants_func tf INNER JOIN sys_func_info func ON tf.FUNC_ID = func.FUNC_ID AND func.IS_VALID = 'Y' "
			+ "WHERE tf.TENANTS_ID = :tenantsId "
			+ "ORDER BY func.PARENT_FUNC_ID, func.DISP_ORDER, func.FUNC_ID";
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllFuncData() {
		Query query = entityManager.createNativeQuery(SQL_GET_ALL_FUNC_DATA); 
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getFuncTreeList(List<Long> idList, String isShow, String folderFlag) {
		StringBuffer querySql = new StringBuffer(SQL_GET_FUNC_TREE_LIST);
		//添加查询参数
		List<String> paramsList = new ArrayList<String>();
		List<Object> valueList = new ArrayList<Object>();
		if(idList != null && idList.size() > 0){
			querySql.append(" AND FUNC_ID IN (:idList) ");
			paramsList.add("idList");
			valueList.add(idList);
		}
		if(!StringUtils.isBlank(isShow)) {
			querySql.append(" AND IS_SHOW =:isShow ");
			paramsList.add("isShow");
			valueList.add(isShow);
        }
		if(!StringUtils.isBlank(folderFlag)) {
			querySql.append(" AND FUNC_TYPE = 100200 AND PARENT_FUNC_ID IS NOT NULL ");
        }
		querySql.append("ORDER BY PARENT_FUNC_ID, DISP_ORDER, FUNC_ID");
		
		//创建查询对象
		Query query = entityManager.createNativeQuery(querySql.toString()); 
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		//设置查询参数
		for(int i = 0; i < paramsList.size(); i++){
			query.setParameter(paramsList.get(i), valueList.get(i));
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getFuncByTenantsId(Long tenantsId) {
		Query query = entityManager.createNativeQuery(SQL_GET_FUNC_BY_TENANTSID); 
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setParameter("tenantsId", tenantsId);
		return query.getResultList();
	}

}
