package com.zjlab.dataservice.modules.tc.event;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventContent implements Serializable {

    private String userCode;

    private String title;

    private String content;

    private String url;

    private Long time;
}
