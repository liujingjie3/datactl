package com.zjlab.dataservice.modules.notify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 通知任务 Mapper
 */
@Mapper
public interface NotifyJobMapper extends BaseMapper<NotifyJob> {

    Long selectIdByDedupKey(@Param("dedupKey") String dedupKey);


    List<NotifyJob> lockDueJobs(@Param("limit") int limit);

    int markSuccess(@Param("id") long id);

    int scheduleRetry(@Param("id") long id,
                      @Param("nextRunTime") LocalDateTime nextRunTime,
                      @Param("retryCount") int retryCount);

    int reschedule(@Param("id") long id,
                   @Param("nextRunTime") LocalDateTime nextRunTime);

    List<Long> selectIdsByBiz(@Param("bizType") byte bizType,
                              @Param("bizIds") Collection<Long> bizIds,
                              @Param("statuses") Collection<Byte> statuses);

    int updateStatusByIds(@Param("ids") Collection<Long> ids,
                          @Param("status") byte status,
                          @Param("operator") String operator,
                          @Param("expected") Collection<Byte> expectedStatuses);

    int logicDeleteByIds(@Param("ids") Collection<Long> ids,
                         @Param("operator") String operator);
}

