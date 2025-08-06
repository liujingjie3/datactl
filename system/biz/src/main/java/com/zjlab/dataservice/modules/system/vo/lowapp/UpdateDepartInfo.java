package com.zjlab.dataservice.modules.system.vo.lowapp;

import com.zjlab.dataservice.modules.system.entity.SysDepart;
import lombok.Data;

import java.util.List;

/**
 * @Author taoYan
 * @Date 2022/12/30 16:25
 **/
@Data
public class UpdateDepartInfo {
    
    private String departId;
    
    private String departName;
    
    private String parentId;
    
    private Boolean hasSub;
    
    public UpdateDepartInfo(){
        
    }

    public UpdateDepartInfo(SysDepart depart){
        this.departId = depart.getId();
        this.departName = depart.getDepartName();
        this.parentId = depart.getParentId();
        this.hasSub = false;
    }

    /**
     * 部门负责人ID
     */
    private List<String> chargePersonList;
}
