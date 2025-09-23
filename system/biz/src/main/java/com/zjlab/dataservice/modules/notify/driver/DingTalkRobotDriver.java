package com.zjlab.dataservice.modules.notify.driver;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zjlab.dataservice.modules.notify.mapper.NotifyChannelConfigMapper;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyChannelConfig;
import com.zjlab.dataservice.modules.notify.model.entity.NotifyRecipient;
import com.zjlab.dataservice.modules.notify.render.RenderedMsg;
import com.zjlab.dataservice.modules.system.entity.SysUser;
import com.zjlab.dataservice.modules.system.mapper.SysUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 钉钉机器人驱动
 */
@Component
public class DingTalkRobotDriver implements NotifyDriver {

    private static final Logger log = LoggerFactory.getLogger(DingTalkRobotDriver.class);
    private static final int TITLE_MAX_LENGTH = 20;

    private final NotifyChannelConfigMapper channelConfigMapper;
    private final RestTemplate restTemplate;
    private final SysUserMapper sysUserMapper;

    private volatile DingTalkConfig cachedConfig;

    public DingTalkRobotDriver(NotifyChannelConfigMapper channelConfigMapper,
                               RestTemplate restTemplate,
                               SysUserMapper sysUserMapper) {
        this.channelConfigMapper = channelConfigMapper;
        this.restTemplate = restTemplate;
        this.sysUserMapper = sysUserMapper;
    }

    @PostConstruct
    public void loadConfigOnStartup() {
        this.cachedConfig = loadConfigFromDb();
        if (this.cachedConfig == null) {
            log.error("dingTalk channel config failed to load during startup");
        }
    }

    @Override
    public byte channel() {
        return 1; // 钉钉
    }

    @Override
    public SendResult send(String userId, RenderedMsg message, JSONObject payload) {
        log.info("DingTalkRobotDriver.send userId={}, title={}, templateId={}, payload={}",
                userId, message == null ? null : message.getTitle(),
                message == null ? null : message.getExternalTemplateId(), payload);

        DingTalkConfig config = ensureConfigLoaded();
        if (config == null) {
            String error = "dingTalk channel config not found or invalid";
            log.error("{}", error);
            return SendResult.failure(error);
        }
        if (message == null || StringUtils.isBlank(message.getExternalTemplateId())) {
            String error = "dingTalk template_id is missing";
            log.error(error);
            return SendResult.failure(error);
        }

        if (StringUtils.isBlank(userId)) {
            String error = "dingTalk recipient userId missing";
            log.error(error);
            return SendResult.failure(error);
        }

        Recipient recipient = resolveRecipient(userId);
        if (recipient == null || recipient.isEmpty()) {
            String error = "dingTalk recipient contact not found";
            log.error("{} userId={}", error, userId);
            return SendResult.failure(error);
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("title", truncateTitle(message.getTitle()));
        requestBody.put("template_id", message.getExternalTemplateId());
        requestBody.put("template_params", buildTemplateParams(payload));
        requestBody.put("userid_list", recipient.getUseridList());
        requestBody.put("agent_id", config.getAgentId());
        requestBody.put("corp_id", config.getCorpId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);
        try {
            ResponseEntity<DingTalkResponse> response = restTemplate.postForEntity(
                    config.getSendUrl(), entity, DingTalkResponse.class);
            DingTalkResponse body = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful()) {
                String error = String.format("dingTalk http status %s", response.getStatusCode());
                log.error(error);
                return SendResult.failure(error);
            }
            if (body == null) {
                String error = "dingTalk empty response";
                log.error(error);
                return SendResult.failure(error);
            }
            if (Boolean.TRUE.equals(body.getSuccess()) && (body.getCode() == null || body.getCode() == 200)) {
                log.info("DingTalkRobotDriver.send success msgId={}",
                        body.getData() == null ? null : body.getData().getMsgid());
                return SendResult.success(body.getData() == null ? null : body.getData().getMsgid());
            }
            String error = String.format("dingTalk send failed code=%s msg=%s", body.getCode(), body.getMsg());
            log.error(error);
            return SendResult.failure(error);
        } catch (Exception e) {
            log.error("DingTalkRobotDriver.send exception", e);
            return SendResult.failure(e.getMessage());
        }
    }

    @Override
    public Map<Long, SendResult> sendBatch(List<NotifyRecipient> recipients, RenderedMsg message, JSONObject payload) {
        List<String> userIds = recipients == null ? Collections.emptyList()
                : recipients.stream().filter(r -> r != null).map(NotifyRecipient::getUserId).collect(Collectors.toList());
        log.info("DingTalkRobotDriver.sendBatch userIds={}, title={}, templateId={}, payload={}",
                userIds, message == null ? null : message.getTitle(),
                message == null ? null : message.getExternalTemplateId(), payload);

        Map<Long, SendResult> results = new HashMap<>();
        if (recipients == null || recipients.isEmpty()) {
            return results;
        }

        DingTalkConfig config = ensureConfigLoaded();
        if (config == null) {
            String error = "dingTalk channel config not found or invalid";
            log.error("{}", error);
            fillFailure(results, recipients, error);
            return results;
        }
        if (message == null || StringUtils.isBlank(message.getExternalTemplateId())) {
            String error = "dingTalk template_id is missing";
            log.error(error);
            fillFailure(results, recipients, error);
            return results;
        }

        Map<String, List<NotifyRecipient>> recipientsByUser = new LinkedHashMap<>();
        for (NotifyRecipient recipient : recipients) {
            if (recipient == null) {
                continue;
            }
            if (StringUtils.isBlank(recipient.getUserId())) {
                String error = "dingTalk recipient userId missing";
                log.error(error);
                if (recipient.getId() != null) {
                    results.put(recipient.getId(), SendResult.failure(error));
                }
                continue;
            }
            recipientsByUser.computeIfAbsent(recipient.getUserId(), k -> new ArrayList<>()).add(recipient);
        }

        if (recipientsByUser.isEmpty()) {
            return results;
        }

        Map<String, Recipient> recipientMap = resolveRecipients(recipientsByUser.keySet());
        List<String> resolvedUserIds = new ArrayList<>();
        Set<String> dingtalkUserIds = new LinkedHashSet<>();
        for (Map.Entry<String, List<NotifyRecipient>> entry : recipientsByUser.entrySet()) {
            String userId = entry.getKey();
            Recipient contact = recipientMap.get(userId);
            if (contact == null || contact.isEmpty()) {
                String error = "dingTalk recipient contact not found";
                log.error("{} userId={}", error, userId);
                fillFailure(results, entry.getValue(), error);
                continue;
            }
            resolvedUserIds.add(userId);
            dingtalkUserIds.addAll(contact.getUseridList());
        }

        if (dingtalkUserIds.isEmpty()) {
            return results;
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("title", truncateTitle(message.getTitle()));
        requestBody.put("template_id", message.getExternalTemplateId());
        requestBody.put("template_params", buildTemplateParams(payload));
        requestBody.put("userid_list", new ArrayList<>(dingtalkUserIds));
        requestBody.put("agent_id", config.getAgentId());
        requestBody.put("corp_id", config.getCorpId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);
        try {
            ResponseEntity<DingTalkResponse> response = restTemplate.postForEntity(
                    config.getSendUrl(), entity, DingTalkResponse.class);
            DingTalkResponse body = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful()) {
                String error = String.format("dingTalk http status %s", response.getStatusCode());
                log.error(error);
                fillFailure(results, collectRecipients(recipientsByUser, resolvedUserIds), error);
                return results;
            }
            if (body == null) {
                String error = "dingTalk empty response";
                log.error(error);
                fillFailure(results, collectRecipients(recipientsByUser, resolvedUserIds), error);
                return results;
            }
            if (Boolean.TRUE.equals(body.getSuccess()) && (body.getCode() == null || body.getCode() == 200)) {
                String msgId = body.getData() == null ? null : body.getData().getMsgid();
                log.info("DingTalkRobotDriver.sendBatch success msgId={}", msgId);
                SendResult success = SendResult.success(msgId);
                for (String userId : resolvedUserIds) {
                    putResult(results, recipientsByUser.get(userId), success);
                }
                return results;
            }
            String error = String.format("dingTalk send failed code=%s msg=%s", body.getCode(), body.getMsg());
            log.error(error);
            fillFailure(results, collectRecipients(recipientsByUser, resolvedUserIds), error);
            return results;
        } catch (Exception e) {
            log.error("DingTalkRobotDriver.sendBatch exception", e);
            fillFailure(results, collectRecipients(recipientsByUser, resolvedUserIds), e.getMessage());
            return results;
        }
    }

    private synchronized DingTalkConfig ensureConfigLoaded() {
        if (cachedConfig == null) {
            cachedConfig = loadConfigFromDb();
        }
        return cachedConfig;
    }

    private DingTalkConfig loadConfigFromDb() {
        LambdaQueryWrapper<NotifyChannelConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(NotifyChannelConfig::getChannel, channel())
                .eq(NotifyChannelConfig::getEnabled, (byte) 1)
                .eq(NotifyChannelConfig::getDelFlag, Boolean.FALSE)
                .orderByDesc(NotifyChannelConfig::getUpdateTime)
                .last("LIMIT 1");
        NotifyChannelConfig config = channelConfigMapper.selectOne(wrapper);
        if (config == null) {
            log.error("No dingTalk channel config found");
            return null;
        }
        return DingTalkConfig.fromJson(config.getConfigJson());
    }

    private Recipient resolveRecipient(String userId) {
        Map<String, Recipient> map = resolveRecipients(Collections.singleton(userId));
        return map.getOrDefault(userId, new Recipient());
    }

    private Map<String, Recipient> resolveRecipients(Iterable<String> userIds) {
        Map<String, Recipient> recipients = new HashMap<>();
        if (userIds == null) {
            return recipients;
        }
        List<String> ids = new ArrayList<>();
        for (String uid : userIds) {
            if (StringUtils.isNotBlank(uid)) {
                ids.add(uid);
            }
        }
        if (ids.isEmpty()) {
            return recipients;
        }
        List<SysUser> users = sysUserMapper.selectBatchIds(ids);
        Map<String, SysUser> userMap = new HashMap<>();
        for (SysUser user : users) {
            if (user != null) {
                userMap.put(user.getId(), user);
            }
        }
        for (String userId : ids) {
            SysUser user = userMap.get(userId);
            if (user == null) {
                continue;
            }
            Recipient recipient = new Recipient();
            if (StringUtils.isNotBlank(user.getUsername())) {
                recipient.getUseridList().add(user.getUsername());
            }
            recipients.put(userId, recipient);
        }
        return recipients;
    }

    private void fillFailure(Map<Long, SendResult> container, List<NotifyRecipient> recipients, String error) {
        if (recipients == null || recipients.isEmpty()) {
            return;
        }
        SendResult failure = SendResult.failure(error);
        for (NotifyRecipient recipient : recipients) {
            if (recipient != null) {
                if (recipient.getId() != null) {
                    container.put(recipient.getId(), failure);
                }
            }
        }
    }

    private List<NotifyRecipient> collectRecipients(Map<String, List<NotifyRecipient>> grouped,
                                                    List<String> userIds) {
        List<NotifyRecipient> recipients = new ArrayList<>();
        if (grouped == null || userIds == null) {
            return recipients;
        }
        for (String userId : userIds) {
            List<NotifyRecipient> list = grouped.get(userId);
            if (list != null) {
                recipients.addAll(list);
            }
        }
        return recipients;
    }

    private void putResult(Map<Long, SendResult> container, List<NotifyRecipient> recipients, SendResult result) {
        if (recipients == null || recipients.isEmpty() || result == null) {
            return;
        }
        for (NotifyRecipient recipient : recipients) {
            if (recipient != null) {
                if (recipient.getId() != null) {
                    container.put(recipient.getId(), result);
                }
            }
        }
    }

    private JSONObject buildTemplateParams(JSONObject payload) {
        JSONObject params = new JSONObject();
        if (payload == null) {
            return params;
        }
        for (String key : payload.keySet()) {
            Object value = payload.get(key);
            params.put(key, value == null ? "" : String.valueOf(value));
        }
        return params;
    }

    private String truncateTitle(String title) {
        if (title == null) {
            return "";
        }
        if (title.length() <= TITLE_MAX_LENGTH) {
            return title;
        }
        String truncated = title.substring(0, TITLE_MAX_LENGTH);
        log.warn("DingTalk message title length {} exceeds limit, truncated to {}", title.length(), truncated);
        return truncated;
    }

    private static class DingTalkConfig {
        private final String sendUrl;
        private final String agentId;
        private final String corpId;

        DingTalkConfig(String sendUrl, String agentId, String corpId) {
            this.sendUrl = sendUrl;
            this.agentId = agentId;
            this.corpId = corpId;
        }

        public String getSendUrl() {
            return sendUrl;
        }

        public String getAgentId() {
            return agentId;
        }

        public String getCorpId() {
            return corpId;
        }

        static DingTalkConfig fromJson(String json) {
            if (StringUtils.isBlank(json)) {
                log.error("dingTalk channel config_json is blank");
                return null;
            }
            JSONObject obj;
            try {
                obj = JSONObject.parseObject(json);
            } catch (Exception e) {
                log.error("dingTalk channel config_json parse failed", e);
                return null;
            }
            if (obj == null) {
                log.error("dingTalk channel config_json parse failed");
                return null;
            }
            String baseUrl = obj.getString("baseUrl");
            if (StringUtils.isBlank(baseUrl)) {
                log.error("dingTalk channel config missing baseUrl");
                return null;
            }
            String agentId = obj.getString("agentId");
            String corpId = obj.getString("corpId");
            if (StringUtils.isBlank(agentId) || StringUtils.isBlank(corpId)) {
                log.error("dingTalk channel config missing agentId/corpId");
                return null;
            }
            return new DingTalkConfig(baseUrl, agentId, corpId);
        }
    }

    private static class Recipient {
        private final List<String> useridList = new ArrayList<>();

        public List<String> getUseridList() {
            return useridList;
        }

        public boolean isEmpty() {
            return useridList.isEmpty();
        }
    }

    private static class DingTalkResponse {
        private Integer code;
        private Boolean success;
        private DingTalkResponseData data;
        private String msg;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public DingTalkResponseData getData() {
            return data;
        }

        public void setData(DingTalkResponseData data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    private static class DingTalkResponseData {
        private String msgid;

        public String getMsgid() {
            return msgid;
        }

        public void setMsgid(String msgid) {
            this.msgid = msgid;
        }
    }
}

