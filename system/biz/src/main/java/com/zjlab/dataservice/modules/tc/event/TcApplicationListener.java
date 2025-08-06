package com.zjlab.dataservice.modules.tc.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.kafka.core.KafkaTemplate;


@Component
@Slf4j
public class TcApplicationListener implements ApplicationContextAware {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "tc-event-topic";

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void onApplicationListener(TcEvent event) {
        log.info("event info: {}", event);
        kafkaTemplate.send(TOPIC, event.getSource());
        log.info("event send success.");
    }

    // 新增一个方法
    public void publishEvent(Object event) {
        if (applicationContext != null) {
            applicationContext.publishEvent(event);
            log.info("event published: {}", event);
        } else {
            log.warn("ApplicationContext is null, cannot publish event.");
        }
    }
}

