package com.zjlab.dataservice.modules.myspace.model.dto.shopcart;

import com.zjlab.dataservice.common.api.page.PageRequest;
import lombok.Data;

@Data
public class CheckSubOrderListDto extends PageRequest {

    private String orderId;

    private String userId;

}
