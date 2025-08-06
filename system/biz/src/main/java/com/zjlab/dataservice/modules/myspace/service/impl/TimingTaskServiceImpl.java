package com.zjlab.dataservice.modules.myspace.service.impl;

import com.zjlab.dataservice.modules.myspace.enums.ImageApplyStatusEnum;
import com.zjlab.dataservice.modules.myspace.enums.SubOrderStatusEnum;
import com.zjlab.dataservice.modules.myspace.mapper.CollectMapper;
import com.zjlab.dataservice.modules.myspace.mapper.MyspaceSubOrderMapper;
import com.zjlab.dataservice.modules.myspace.model.po.CollectPo;
import com.zjlab.dataservice.modules.myspace.model.po.MyspaceSubOrderPo;
import com.zjlab.dataservice.modules.myspace.service.TimingTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@Service
@Slf4j
public class TimingTaskServiceImpl implements TimingTaskService {

    private static final Long EXPIRED_DAYS = 7L;

    @Resource
    private CollectMapper collectMapper;
    @Resource
    private MyspaceSubOrderMapper myspaceSubOrderMapper;
    @Override
    public void changeApplyStatus(Integer id, LocalDateTime updateTime) {
        LocalDateTime nextTime = updateTime.plusDays(EXPIRED_DAYS);
        // 指定时区并转换为 Date
        ZoneId zoneId = ZoneId.systemDefault(); // 使用系统默认时区
        Date executeDate = Date.from(nextTime.atZone(zoneId).toInstant());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("will update expired applyStatus, id = {}, executeDate = {}", id, executeDate);
                CollectPo collectPo = collectMapper.selectById(id);
                if (collectPo == null){
                    log.error("Collect Not found. id = {}", id);
                    return;
                }
                if (!Objects.equals(collectPo.getApplyStatus(), ImageApplyStatusEnum.APPLIED.getStatus())){
                    log.error("Collect applyStatus error. required = 2, applyStatus = {}", collectPo.getApplyStatus());
                    return;
                }
                collectPo.setApplyStatus(ImageApplyStatusEnum.EXPIRED.getStatus());
                collectPo.setUpdateTime(nextTime);
                collectMapper.updateById(collectPo);
                log.info("update expired applyStatus success!");
            }
        }, executeDate);
    }


    @Override
    public void changeSubOrderStatus(Integer id, LocalDateTime updateTime) {
        LocalDateTime nextTime = updateTime.plusDays(EXPIRED_DAYS);
        // 指定时区并转换为 Date
        ZoneId zoneId = ZoneId.systemDefault(); // 使用系统默认时区
        Date executeDate = Date.from(nextTime.atZone(zoneId).toInstant());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("will update expired SubOrderStatus, id = {}, executeDate = {}", id, executeDate);
                 MyspaceSubOrderPo myspaceSubOrderPo = myspaceSubOrderMapper.selectById(id);
                if (myspaceSubOrderPo == null){
                    log.error("SubOrder Not found. id = {}", id);
                    return;
                }
                if (!Objects.equals(myspaceSubOrderPo.getSubOrderStatus(), SubOrderStatusEnum.DONE.getStatus())){
                    log.error("SubOrder SubOrderStatus error. required = 2, SubOrderStatus = {}", myspaceSubOrderPo.getSubOrderStatus());
                    return;
                }
                myspaceSubOrderPo.setSubOrderStatus(SubOrderStatusEnum.LOSS.getStatus());
                myspaceSubOrderPo.setUpdateTime(nextTime);
                myspaceSubOrderMapper.updateById(myspaceSubOrderPo);
                log.info("update expired SubOrderStatus success!");
            }
        }, executeDate);
    }

}
