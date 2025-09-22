package com.zjlab.dataservice.modules.notify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyRecipient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 收件人 Mapper
 */
@Mapper
public interface NotifyRecipientMapper extends BaseMapper<NotifyRecipient> {

    int batchInsert(@Param("jobId") long jobId,
                    @Param("userIds") List<String> userIds,
                    @Param("operator") String operator);

    int deleteByJobId(@Param("jobId") long jobId);

    List<NotifyRecipient> findByJobId(@Param("jobId") long jobId);

    int updateStatus(@Param("id") long id,
                     @Param("success") boolean success,
                     @Param("error") String error);

    int updateStatusByJobIds(@Param("jobIds") Collection<Long> jobIds,
                             @Param("status") byte status,
                             @Param("operator") String operator,
                             @Param("expected") Collection<Byte> expectedStatuses);

    int logicDeleteByJobIds(@Param("jobIds") Collection<Long> jobIds,
                            @Param("operator") String operator);
}

