package com.zjlab.dataservice.modules.tc.event;

import com.zjlab.dataservice.common.util.Func;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class EventEntity<T> implements Serializable {

    //    UUID without dashes: e7b8f94e4a1c4c77b9d017d99c2f3a5e
    private String code = Func.randomUUID();

    private String source = "TC";

    private String eventType;

    private Long eventTime = System.currentTimeMillis();

    private String corpId;

    private String agentId;

    private T context;
}
