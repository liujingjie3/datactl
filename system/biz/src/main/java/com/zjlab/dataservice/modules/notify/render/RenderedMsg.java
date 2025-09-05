package com.zjlab.dataservice.modules.notify.render;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 渲染后的消息
 */
@Data
@AllArgsConstructor
public class RenderedMsg {
    private String title;
    private String content;
}

