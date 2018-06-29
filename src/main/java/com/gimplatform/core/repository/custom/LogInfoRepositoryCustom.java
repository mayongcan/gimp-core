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
public interface LogInfoRepositoryCustom {

    /**
     * 获取日志列表详情
     * @param userInfo
     * @param params
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getLogList(UserInfo userInfo, Map<String, Object> params, int pageIndex, int pageSize);

    /**
     * 获取日志列表详情 分页数量
     * @param userInfo
     * @param params
     * @return
     */
    public int getLogListCount(UserInfo userInfo, Map<String, Object> params);

}
