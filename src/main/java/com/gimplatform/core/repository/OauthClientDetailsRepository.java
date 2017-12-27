package com.gimplatform.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.gimplatform.core.entity.OauthClientDetails;

/**
 * 授权信息资源操作类
 * @author zzd
 *
 */
@Repository
public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails, String>, JpaSpecificationExecutor<OauthClientDetails> {

	/**
	 * 根据clientId查找授权客户端
	 * @param clientId
	 * @return
	 */
	public List<OauthClientDetails> findByClientId(String clientId);

}
