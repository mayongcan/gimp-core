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
public interface UserLogonRepositoryCustom {

    /**
     * 获取已锁账号列表详情
     * @param userInfo
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getLockAccount(UserInfo userInfo, int pageIndex, int pageSize);

    /**
     * 获取已锁账号数量
     * @param userInfo
     * @return
     */
    public int getLockAccountCount(UserInfo userInfo);

}
