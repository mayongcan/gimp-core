package com.gimplatform.core.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * oauth授权客户端列表
 * @author zzd
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth_client_details")
public class OauthClientDetails implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "client_id", unique = true, nullable = false, length = 64)
	private String clientId;

	@Column(name = "resource_ids", length = 256)
	private String resourceIds;

	@Column(name = "client_secret", length = 256)
	private String clientSecret;

	@Column(name = "scope", length = 256)
	private String scope;

	@Column(name = "authorized_grant_types", length = 256)
	private String authorizedGrantTypes;

	@Column(name = "web_server_redirect_uri", length = 256)
	private String webServerRedirectUri;

	@Column(name = "authorities", length = 256)
	private String authorities;

	@Column(name = "access_token_validity", precision = 10, scale = 0)
	private Long accessTokenValidity;

	@Column(name = "refresh_token_validity", precision = 10, scale = 0)
	private Long refreshTokenValidity;

	@Column(name = "additional_information", length = 4096)
	private String additionalInformation;

	@Column(name = "autoapprove", length = 256)
	private String autoapprove;

}
