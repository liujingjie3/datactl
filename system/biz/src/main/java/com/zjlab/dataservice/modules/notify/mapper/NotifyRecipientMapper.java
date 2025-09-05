package com.zjlab.dataservice.modules.notify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyRecipient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收件人 Mapper
 */
@Mapper
public interface NotifyRecipientMapper extends BaseMapper<NotifyRecipient> {

    int batchInsert(@Param("jobId") long jobId,
                    @Param("userIds") List<String> userIds,
                    @Param("operator") String operator);

    List<NotifyRecipient> findByJobId(@Param("jobId") long jobId);

    int updateStatus(@Param("id") long id,
                     @Param("success") boolean success,
                     @Param("error") String error);
}

