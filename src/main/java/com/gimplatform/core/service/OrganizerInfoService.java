package com.gimplatform.core.service;

import java.util.Date;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.OrganizerInfo;
import com.gimplatform.core.entity.TenantsInfo;
import com.gimplatform.core.entity.UserInfo;

/**
 * 组织信息服务类
 * @author zzd
 */
public interface OrganizerInfoService {

    /**
     * 获取组织
     * @param organizerId
     * @return
     */
    public OrganizerInfo getByOrganizerId(Long organizerId);

    /**
     * 通过父ID获取组织树
     * @param parentOrgId
     * @param filterDept
     * @param filterPost
     * @return
     */
    public JSONArray getOrganizerTreeByParentId(Map<String, Object> params);

    /**
     * 通过组织ID获取组织树（如果parentOrgId不为null则调用getOrganizerTreeByParentId）
     * @param userInfo
     * @param params
     * @return
     */
    public JSONArray getOrganizerTreeById(UserInfo userInfo, Map<String, Object> params);

    /**
     * 通过租户ID获取组织树（如果parentOrgId不为null则调用getOrganizerTreeByParentId）
     * @param userInfo
     * @param params
     * @return
     */
    public JSONArray getOrganizerTreeByTenantsId(UserInfo userInfo, Map<String, Object> params);

    /**
     * 通过租户信息创建组织(组织根节点)
     * @param tenantsInfo
     * @param userinfo
     */
    public void addOrganizerByTenants(TenantsInfo tenantsInfo, UserInfo userInfo);

    /**
     * 通过组织信息修改组织(组织根节点)
     * @param tenantsInfo
     * @param userinfo
     */
    public void editOrganizerByTenants(TenantsInfo tenantsInfo, UserInfo userInfo);

    /**
     * 新增组织
     * @param organizerInfo
     * @param userInfo
     * @return
     */
    public JSONObject addOrganizer(OrganizerInfo organizerInfo, UserInfo userInfo, Map<String, Object> params);

    /**
     * 编辑组织
     * @param organizerInfo
     * @param userInfo
     * @return
     */
    public JSONObject editOrganizer(OrganizerInfo organizerInfo, UserInfo userInfo, Map<String, Object> params);

    /**
     * 删除组织
     * @param idsList
     * @param userInfo
     * @return
     */
    public JSONObject delOrganizer(String idsList, UserInfo userInfo);

    /**
     * 通过组织ID和类型获取所属的组织（如需要查找组织类型为1的上级组织，递归查找）
     * @param orgId
     * @return
     */
    public Long getOrganizerByIdAndType(Long orgId, int type);

    /**
     * 获取父节点
     * @param organizerId
     * @return
     */
    public Long getOrganizerParentId(Long organizerId);

    /**
     * 更新开始时间
     * @param tenantsId
     * @param beginDate
     */
    public void updateBeginDateByTenantsId(Long tenantsId, Date beginDate);

    /**
     * 更新开始时间
     * @param tenantsId
     * @param endDate
     */
    public void updateEndDateByTenantsId(Long tenantsId, Date endDate);

    /**
     * 获取用户的部门负责人ID
     * @param userInfo
     * @return
     */
    public Long getOrganizerManagerIdByUser(UserInfo userInfo);

    /**
     * 根据租户ID获取根组织
     * @param tenantsId
     * @return
     */
    public OrganizerInfo getRootOrgByTenantsId(Long tenantsId);

    /**
     * 获取默认角色权限和数据权限
     * @param organizerId
     * @return
     */
    public JSONObject getRoleAndData(Long organizerId);

    /**
     * 获取组织的额外信息，包括父组织名称，默认角色名称，默认数据权限名称
     * @param organizerId
     * @param parentOrgId
     * @return
     */
    public JSONObject getExtraInfo(Long organizerId, Long parentOrgId);

    /**
     * 获取组织数据列表
     * @param page
     * @param organizerInfo
     * @return
     */
    public Page<OrganizerInfo> getOrgainzerList(Pageable page, OrganizerInfo organizerInfo);

    public JSONObject submitOrganizerCache(OrganizerInfo organizerInfo, UserInfo userInfo, Map<String, Object> params);

    public JSONObject auditPass(String idsList, UserInfo userInfo);

    public JSONObject auditNotPass(String idsList, UserInfo userInfo);

    /**
     * 判断用户是否组织管理员
     * @param userInfo
     * @return
     */
    public boolean isOrganizerManager(UserInfo userInfo);
}
