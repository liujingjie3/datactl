package com.zjlab.dataservice.modules.taskplan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjlab.dataservice.common.constant.enums.ResultCode;
import com.zjlab.dataservice.common.exception.BaseException;
import com.zjlab.dataservice.common.threadlocal.UserThreadLocal;
import com.zjlab.dataservice.modules.taskplan.model.dto.TcTaskAddDto;
import com.zjlab.dataservice.modules.taskplan.model.po.TcCxtaskPo;
import com.zjlab.dataservice.modules.taskplan.service.TcCxtaskService;
import com.zjlab.dataservice.modules.taskplan.mapper.TcCxtaskMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class TcCxtaskServiceImpl extends ServiceImpl<TcCxtaskMapper, TcCxtaskPo>
    implements TcCxtaskService {

    @Override
    public int addTcTask(TcTaskAddDto tcTaskAddDto) {
        TcCxtaskPo tcCxtaskPo  = new TcCxtaskPo();

        BeanUtils.copyProperties(tcTaskAddDto, tcCxtaskPo);

        tcCxtaskPo.setSatelliteInfo(tcTaskAddDto.getSatelliteInfo().toString());
        tcCxtaskPo.setComandList(tcTaskAddDto.getComandList().toString());
        tcCxtaskPo.setStatus(1);//状态需要设计 待定
        String userId = UserThreadLocal.getUserId();
        tcCxtaskPo.initBase(true, userId);

        int num = baseMapper.insert(tcCxtaskPo);
        if (num <= 0) {
            throw new BaseException(ResultCode.SQL_UPDATE_ERROR);
        }
        return tcCxtaskPo.getId();
    }
}




