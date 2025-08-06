package com.zjlab.dataservice.modules.tc.event;

import org.springframework.context.ApplicationEvent;

public class TcEvent extends ApplicationEvent {
    private static final long serialVersionUID = -3703477845788106064L;

    public TcEvent(Object source) {
        super(source);
    }
}
