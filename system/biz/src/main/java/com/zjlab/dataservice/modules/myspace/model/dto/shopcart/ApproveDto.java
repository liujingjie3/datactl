package com.zjlab.dataservice.modules.myspace.model.dto.shopcart;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

@Data
public class ApproveDto {

    private Integer id;

    private Integer subOrderStatus;


    private String approvalOpinion;

    private String fileUrl;
}
