package com.gimplatform.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.RoleInfo;
import com.gimplatform.core.entity.UserInfo;

/**
 * 角色信息服务类
 * @author zzd
 */
public interface RoleInfoService {

    /**
     * 根据用户code查找角色名称列表
     * @param userCode
     * @return
     */
    public List<String> getRolesNameByUser(UserInfo userInfo);

    /**
     * 获取角色列表
     * @param page
     * @param roleInfo
     * @return
     */
    public Page<RoleInfo> getRoleList(Pageable page, RoleInfo roleInfo);

    /**
     * 获取返回分页数据的角色用户列表
     * @param page
     * @param tenantsId
     * @param organizerId
     * @param roleId
     * @param dataType
     * @return
     */
    public JSONObject getRoleUserList(Pageable page, Map<String, Object> params);

    /**
     * 根据角色ID获取角色所属权限
     * @param userInfo
     * @param roleId
     * @return
     */
    public JSONObject getFuncTreeByRoleId(UserInfo userInfo, Long roleId);

    /**
     * 新增角色
     * @param roleInfo
     * @param userInfo
     * @return
     */
    public JSONObject addRole(RoleInfo roleInfo, UserInfo userInfo);

    /**
     * 编辑角色
     * @param roleInfo
     * @param userInfo
     * @return
     */
    public JSONObject editRole(RoleInfo roleInfo, UserInfo userInfo);

    /**
     * 删除角色
     * @param roleId
     * @param userInfo
     * @return
     */
    public JSONObject delRole(Long roleId, UserInfo userInfo);

    /**
     * 保存角色权限
     * @param userInfo
     * @param roleId
     * @param listFuncId
     * @return
     */
    public JSONObject saveRoleFunc(UserInfo userInfo, Long roleId, List<Long> listFuncId);

    /**
     * 保存角色用户
     * @param userInfo
     * @param roleId
     * @param userIdList
     * @return
     */
    public JSONObject addUserRole(UserInfo userInfo, Long roleId, List<Long> userIdList);

    /**
     * 删除角色用户
     * @param userInfo
     * @param roleId
     * @param userIdList
     * @return
     */
    public JSONObject delUserRole(UserInfo userInfo, Long roleId, List<Long> userIdList);

    /**
     * @param organizerId
     * @return
     */
    public JSONObject getRolesKeyValByOrganizerId(Long organizerId);

}
