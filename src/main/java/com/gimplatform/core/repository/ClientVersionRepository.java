package com.gimplatform.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.gimplatform.core.entity.ClientVersion;

@Repository
public interface ClientVersionRepository extends JpaRepository<ClientVersion, Long>, JpaSpecificationExecutor<ClientVersion> {

}
