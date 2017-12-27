package com.gimplatform.core.repository.impl;

import java.util.List;
import java.util.Map;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.DictDataRepositoryCustom;

@Transactional
public class DictDataRepositoryImpl extends BaseRepository implements DictDataRepositoryCustom{

	//获取所有字典数据
	private static final String SQL_GET_ALL_DICT_DATA = "SELECT DISTINCT DD.VALUE AS ID, DD.NAME AS NAME, DT.VALUE AS DICTTYPE, DD.TENANTS_ID AS TID, "
			+ "DD.ORGANIZER_ID AS OID, DD.DISP_ORDER AS SORTID "
			+ "FROM SYS_DICT_TYPE DT INNER JOIN SYS_DICT_DATA DD ON DT.DICT_TYPE_ID = DD.DICT_TYPE_ID "
			+ "WHERE DT.IS_VALID = 'Y' AND DD.IS_VALID = 'Y' "
			+ "ORDER BY DICTTYPE, SORTID ";
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllDictData() {
		Query query = entityManager.createNativeQuery(SQL_GET_ALL_DICT_DATA); 
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}

}
