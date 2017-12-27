package com.gimplatform.core.repository.impl;

import java.util.Map;
import javax.persistence.Query;
import org.apache.commons.collections4.MapUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.TbGeneratorRepositoryCustom;

public class TbGeneratorRepositoryImpl extends BaseRepository implements TbGeneratorRepositoryCustom{

	@SuppressWarnings("unchecked")
	public String getMaxId(String primaryKey, String table) {
		StringBuffer querySql = new StringBuffer("SELECT MAX(" + primaryKey + ") as \"maxId\" FROM " + table);
        //创建查询对象
		Query query = entityManager.createNativeQuery(querySql.toString()); 
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		Map<String, Object> map = (Map<String, Object>) query.getSingleResult();
		return MapUtils.getString(map, "maxId");
	}

}
