package com.zjlab.dataservice.modules.backup.schedule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjlab.dataservice.modules.backup.model.po.TaskPo;
import com.zjlab.dataservice.modules.backup.service.TaskService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Component
@Slf4j
public class ScheduleConfig {

    public ConcurrentHashMap<Integer, String> scheduleCronMap = new ConcurrentHashMap<>();

    @Resource
    private TaskService taskService;
    @Value("${spring.profiles.active}")
    private String activeProfile;
    @PostConstruct
    public void initTask(){
        if (Objects.equals(activeProfile, "dev")){
            //测试环境为windows环境的数据库，切内存不够，暂不操作
            log.info("environment dev, do nothing and return.");
            return;
        }
        log.info("will init schedule task.");
        QueryWrapper<TaskPo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TaskPo::getDelFlag, false);
        List<TaskPo> list = taskService.list(wrapper);
        for (TaskPo task : list){
            Integer id = task.getId();
            String cron = task.getCron();   //todo 非周期性任务处理
            scheduleCronMap.put(id, cron);
        }
        log.info("init schedule task success! {}", scheduleCronMap);
    }

    public void changeCron(Integer taskId, String newCron){
        String oldCron = scheduleCronMap.get(taskId);
        scheduleCronMap.put(taskId, newCron);
        log.info("change task cron. taskId={}, oldCron={}, newCron={}", taskId, oldCron, newCron);
    }
}
