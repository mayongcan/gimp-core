package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * 自定义扩展资源类接口
 * @author zzd
 */
@NoRepositoryBean
public interface RoleInfoRepositoryCustom {

    /**
     * 获取用户角色列表
     * @param params
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getRoleUserList(Map<String, Object> params, int pageIndex, int pageSize);

    /**
     * 获取用户角色列表数量
     * @param params
     * @return
     */
    public int getRoleUserListCount(Map<String, Object> params);

    /**
     * 获取KeyVal
     * @param organizerId
     * @return
     */
    public List<Map<String, Object>> getRolesKeyValByOrganizerId(Long organizerId);
}
