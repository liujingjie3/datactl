package com.zjlab.dataservice.modules.myspace.service;

import java.time.LocalDateTime;

public interface TimingTaskService {

    /**
     * 订单申请状态修改，申请期限 7 天，将申请状态由申请完成改为已失效
     * @param id  收藏表id
     * @param updateTime  更新时间
     */
    void changeApplyStatus(Integer id, LocalDateTime updateTime);
    void changeSubOrderStatus(Integer id, LocalDateTime updateTime);

}
