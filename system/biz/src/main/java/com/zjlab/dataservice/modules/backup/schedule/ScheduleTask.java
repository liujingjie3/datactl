package com.zjlab.dataservice.modules.backup.schedule;

import com.zjlab.dataservice.modules.backup.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleTask implements SchedulingConfigurer {

    @Resource
    private TaskService taskService;
    @Resource
    private ScheduleConfig scheduleConfig;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (Objects.equals(activeProfile, "dev")){
            //测试环境为windows环境的数据库，切内存不够，暂不操作
            log.info("environment dev, do nothing and return.");
            return;
        }
        ConcurrentHashMap<Integer, String> scheduleCronMap = scheduleConfig.getScheduleCronMap();

        //设置任务线程池
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        taskRegistrar.setScheduler(scheduledExecutorService);

        //遍历注册定时任务
        for (Integer taskId : scheduleCronMap.keySet()){
            taskRegistrar.addTriggerTask(
                    //添加任务内容
                    () -> {
                        try {
                            taskService.start(taskId);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        taskService.delExpire(taskId);
                        log.info("execute schedule: taskId={}, time={}", taskId, LocalDateTime.now());
                    },
                    //设置执行周期
                    triggerContext -> {
                        //获取执行周期
                        String cron = scheduleCronMap.get(taskId);
                        //合法性校验
                        //返回执行周期
                        return new CronTrigger(cron).nextExecutionTime(triggerContext);
                    }
            );
        }
    }
}
