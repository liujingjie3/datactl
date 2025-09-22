package com.zjlab.dataservice.modules.notify.render;

import lombok.Data;

/**
 * 渲染后的消息
 */
@Data
public class RenderedMsg {
    private final String title;
    private final String content;
    /**
     * 三方平台模板ID（如钉钉模板ID），用于驱动发送。
     */
    private final String externalTemplateId;

    public RenderedMsg(String title, String content) {
        this(title, content, null);
    }

    public RenderedMsg(String title, String content, String externalTemplateId) {
        this.title = title;
        this.content = content;
        this.externalTemplateId = externalTemplateId;
    }
}

