package com.zjlab.dataservice.modules.notify.render;

import com.alibaba.fastjson.JSONObject;
import com.zjlab.dataservice.modules.notify.mapper.NotifyTemplateMapper;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 基于数据库模板的渲染器
 */
@Component
public class TemplateNotifyRenderer implements NotifyRenderer {

    @Autowired
    private NotifyTemplateMapper templateMapper;

    @Override
    public RenderedMsg render(byte bizType, byte channel, JSONObject payload) {
        NotifyTemplate tpl = templateMapper.selectByBizAndChannel(bizType, channel);
        if (tpl == null) {
            return new RenderedMsg("", "");
        }
        String title = replace(tpl.getTitleTpl(), payload);
        String content = replace(tpl.getContentTpl(), payload);
        return new RenderedMsg(title, content, tpl.getExternalTplId());
    }

    private String replace(String template, JSONObject payload) {
        String result = template;
        for (Map.Entry<String, Object> e : payload.entrySet()) {
            String key = "${" + e.getKey() + "}";
            String val = e.getValue() == null ? "" : String.valueOf(e.getValue());
            result = result.replace(key, val);
        }
        return result;
    }
}

