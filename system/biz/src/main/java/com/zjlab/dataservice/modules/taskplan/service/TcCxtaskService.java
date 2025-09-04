package com.zjlab.dataservice.modules.taskplan.service;

import com.zjlab.dataservice.modules.taskplan.model.dto.TcTaskAddDto;
import com.zjlab.dataservice.modules.taskplan.model.po.TcCxtaskPo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TcCxtaskService extends IService<TcCxtaskPo> {
    int addTcTask(TcTaskAddDto tcTaskAddDto);

}
