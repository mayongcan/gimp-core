package com.gimplatform.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.TenantsInfo;
import com.gimplatform.core.entity.UserInfo;

/**
 * 租户信息服务类
 * @author zzd
 */
public interface TenantsInfoService {

    /**
     * 获取租户
     * @param tenantsId
     * @return
     */
    public TenantsInfo getByTenantsId(Long tenantsId);

    /**
     * 获取所有租户列表数据（只需要ID和Name）
     * @param tenantsInfo
     * @return
     */
    public JSONObject getAllTenantsList();

    /**
     * 获取租户列表
     * @param tenantsInfo
     * @return
     */
    public Page<TenantsInfo> getTenantsList(Pageable page, TenantsInfo tenantsInfo);

    /**
     * 新增租户
     * @param tenantsInfo
     * @param userInfo
     * @return
     */
    public JSONObject addTenants(TenantsInfo tenantsInfo, UserInfo userInfo);

    /**
     * 编辑租户
     * @param tenantsInfo
     * @param userInfo
     * @return
     */
    public JSONObject editTenants(TenantsInfo tenantsInfo, UserInfo userInfo);

    /**
     * 删除租户
     * @param idsList
     * @param userInfo
     * @return
     */
    public JSONObject delTenants(String idsList, UserInfo userInfo);

    /**
     * 保存租户权限
     * @param userInfo
     * @param tenantsId
     * @param listFuncId
     * @return
     */
    public JSONObject saveTenantsFunc(UserInfo userInfo, Long tenantsId, List<Long> listFuncId);
}
