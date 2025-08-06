package com.zjlab.dataservice.modules.dataset.model.vo.thematic;

import com.zjlab.dataservice.modules.dataset.factory.HeadInfo;
import com.zjlab.dataservice.modules.dataset.model.entity.StatisticsEntity;
import lombok.Data;

import java.util.List;

@Data
public class StaticsVo {
    private List<HeadInfo> head;
    private List<StatisticsEntity> data;
}
