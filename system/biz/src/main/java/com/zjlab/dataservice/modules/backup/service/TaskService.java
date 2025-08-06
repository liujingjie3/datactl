package com.zjlab.dataservice.modules.backup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.modules.backup.model.po.TaskPo;

public interface TaskService extends IService<TaskPo> {

    //启动，立即执行
    void start(Integer id) throws Exception;

    //清除过期备份
    void delExpire(Integer id);
}
