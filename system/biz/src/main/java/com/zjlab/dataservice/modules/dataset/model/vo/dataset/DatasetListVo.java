package com.zjlab.dataservice.modules.dataset.model.vo.dataset;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DatasetListVo {

    private Integer id;

    private String name;

    private String title;

    private String introduction;

    private String thumbUrl;

    private String oriUrl;

    private String datasetCopy;

    private Long datasetSize;

    private Long sampleSum;

    private String tags;

    private List<JSONObject> tagsObj;

    private Integer viewNum;

    private Integer downloadNum;

    private Integer collectNum;

    private Integer subscribeNum;
}
