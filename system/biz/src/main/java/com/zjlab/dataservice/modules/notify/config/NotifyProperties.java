package com.zjlab.dataservice.modules.notify.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Notification related configuration properties.
 */
@Component
@ConfigurationProperties(prefix = "notify")
public class NotifyProperties {

    /**
     * URL of the 星地协同平台.
     */
    private String platformUrl;

    public String getPlatformUrl() {
        return platformUrl;
    }

    public void setPlatformUrl(String platformUrl) {
        this.platformUrl = platformUrl;
    }
}

