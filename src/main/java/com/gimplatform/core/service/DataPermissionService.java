package com.gimplatform.core.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.DataPermission;
import com.gimplatform.core.entity.UserInfo;

/**
 * 数据权限信息服务类
 * @author zzd
 */
public interface DataPermissionService {

    /**
     * 获取返回分页数据的数据权限用户列表
     * @param page
     * @param params
     * @return
     */
    public JSONObject getUserListByDataPermission(Pageable page, Map<String, Object> params);

    /**
     * 获取树内容
     * @param tenantsId
     * @param organizerId
     * @return
     */
    public JSONObject getTreeList(Long tenantsId, Long organizerId);

    /**
     * 获取树内容
     * @param tenantsId
     * @param organizerId
     * @return
     */
    public JSONObject getRootTreeList(Long tenantsId, Long organizerId);

    /**
     * 根据父标志，获取列表
     * @param dataPermission
     * @return
     */
    public List<DataPermission> getListByParentIds(DataPermission dataPermission);

    /**
     * 新增数据权限
     * @param dataPermission
     * @param userInfo
     * @return
     */
    public JSONObject addDataPermission(DataPermission dataPermission, UserInfo userInfo);

    /**
     * 编辑数据权限
     * @param dataPermission
     * @param userInfo
     * @return
     */
    public JSONObject editDataPermission(DataPermission dataPermission, UserInfo userInfo);

    /**
     * 删除数据权限
     * @param permissionId
     * @param userInfo
     * @return
     */
    public JSONObject delDataPermission(Long permissionId, UserInfo userInfo);

    /**
     * 保存用户数据权限
     * @param userInfo
     * @param permissionId
     * @param userIdList
     * @return
     */
    public JSONObject addUserDataPermission(UserInfo userInfo, Long permissionId, List<Long> userIdList);

    /**
     * 删除数据权限用户
     * @param userInfo
     * @param permissionId
     * @param userIdList
     * @return
     */
    public JSONObject delUserDataPermission(UserInfo userInfo, Long permissionId, List<Long> userIdList);

    /**
     * 获取树内容
     * @param parentId
     * @return
     */
    public List<DataPermission> getTreeListByParentId(Long parentId);

    /**
     * 获取树内容
     * @param parentId
     * @return
     */
    public Map<String, String> getTreeListByUser(UserInfo userInfo);
}
