package com.zjlab.dataservice.modules.notify.render;

import com.alibaba.fastjson.JSONObject;

/**
 * 模板渲染器
 */
public interface NotifyRenderer {
    RenderedMsg render(byte bizType, byte channel, JSONObject payload);
}

