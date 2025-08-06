package com.zjlab.dataservice.modules.system.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Component
public class OuathUserInfo {
    private String mobile;
    private String realname;
    private String uid;
    private String randomId;
    private String email;
    private String username;
}
