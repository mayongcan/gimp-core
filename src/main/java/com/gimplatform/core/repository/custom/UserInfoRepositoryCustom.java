package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;
import org.springframework.data.repository.NoRepositoryBean;

import com.gimplatform.core.entity.UserInfo;

/**
 * 自定义扩展资源类接口
 * @author zzd
 */
@NoRepositoryBean
public interface UserInfoRepositoryCustom {

    // /**
    // * 获取用户权限菜单
    // * @param userId
    // * @param tenantsId
    // * @return
    // */
    // public List<Map<String, Object>> getUserFunc(Long userId, Long tenantsId);
    //
    // /**
    // * 根据FD获取用户权限菜单
    // * @param userId
    // * @param folderId
    // * @return
    // */
    // public List<Map<String, Object>> getUserFuncByFd(Long userId, Long folderId);

    /**
     * 获取用户列表详情
     * @param userInfo
     * @param params
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getUserList(UserInfo userInfo, Map<String, Object> params, int pageIndex, int pageSize);

    /**
     * 获取用户列表详情数量
     * @param userInfo
     * @param params
     * @return
     */
    public int getUserListCount(UserInfo userInfo, Map<String, Object> params);

    /**
     * 查找当前组织当前租户下的用户（包括子组织）
     * @param userInfo
     * @return
     */
    public List<Map<String, Object>> getUserKeyVal(UserInfo userInfo);

}
