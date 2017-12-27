package com.gimplatform.core.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.TbGenerator;
import com.gimplatform.core.repository.custom.TbGeneratorRepositoryCustom;

@Repository
public interface TbGeneratorRepository extends JpaRepository<TbGenerator, String>, JpaSpecificationExecutor<TbGenerator>, TbGeneratorRepositoryCustom{

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE sys_tb_generator SET GEN_VALUE=:genValue WHERE ID =:id", nativeQuery = true)
	public void updateGenValue(@Param("id") Long id, @Param("genValue") Long genValue);
}
