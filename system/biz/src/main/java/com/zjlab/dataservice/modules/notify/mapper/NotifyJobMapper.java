package com.zjlab.dataservice.modules.notify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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
}

