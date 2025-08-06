package com.zjlab.dataservice.modules.dataset.factory;

import java.util.ArrayList;
import java.util.List;

public class HeadInfoFactory {
    public static List<HeadInfo> createHeadInfoList() {
        List<HeadInfo> headInfoList = new ArrayList<>();
        headInfoList.add(new HeadInfo(1, "id", "分类号"));
        headInfoList.add(new HeadInfo(2, "name_cn", "类别名称"));
        headInfoList.add(new HeadInfo(3, "name_en", "类别英文名称"));
        headInfoList.add(new HeadInfo(4, "totalArea", "面积"));
        headInfoList.add(new HeadInfo(5, "count", "图斑数量"));
        headInfoList.add(new HeadInfo(6, "percent", "面积占比"));
        return headInfoList;
    }
}
