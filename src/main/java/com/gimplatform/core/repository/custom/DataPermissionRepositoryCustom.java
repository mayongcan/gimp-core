package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 自定义扩展资源类接口
 * @author zzd
 */
@NoRepositoryBean
public interface DataPermissionRepositoryCustom {

    /**
     * 获取用户数据权限列表
     * @param params
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getUserListByDataPermission(Map<String, Object> params, int pageIndex, int pageSize);

    /**
     * 获取用户数据权限列表数
     * @param params
     * @return
     */
    public int getUserListCountByDataPermission(Map<String, Object> params);

    /**
     * 获取所有用户
     * @param dataPermissionId
     * @return
     */
    public List<Map<String, Object>> getAllUserListByDataPermission(Long dataPermissionId);
}
