
package com.zjlab.dataservice.common.util.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StorageUnit {
    private double value;
    private String unit;

}
