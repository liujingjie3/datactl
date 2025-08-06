package com.zjlab.dataservice.modules.dataset.model.vo.thematic;

import com.zjlab.dataservice.modules.dataset.model.po.ThematicJZHPo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ThematicMapVo {

    private List<ThematicTypeVo> thematicTypes;

    private List<ThematicJZHPo> thematicMap;
}
