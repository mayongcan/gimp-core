package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;
import org.springframework.data.repository.NoRepositoryBean;
import com.gimplatform.core.entity.scheduler.QrtzFiredDetails;

@NoRepositoryBean
public interface QrtzFiredDetailsRepositoryCustom {

    /**
     * 获取列表
     * @param qrtzFiredDetails
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getList(QrtzFiredDetails qrtzFiredDetails, int pageIndex, int pageSize);

    /**
     * 获取列表总数
     * @param qrtzFiredDetails
     * @return
     */
    public int getListCount(QrtzFiredDetails qrtzFiredDetails);
}
