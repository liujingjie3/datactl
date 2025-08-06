package com.zjlab.dataservice.modules.base.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import com.zjlab.dataservice.common.api.dto.LogDTO;

/**
 * @Description: BaseCommonMapper
 * @author: jeecg-boot
 */
public interface BaseCommonMapper {

    /**
     * 保存日志
     * @param dto
     */
    @InterceptorIgnore(illegalSql = "true", tenantLine = "true")
    void saveLog(@Param("dto")LogDTO dto);

}
