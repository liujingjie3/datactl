//package com.zjlab.dataservice.modules.taskplan.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CYKConsumerService {
//
//    @Value("${kafka.topic.my-topic}")
//    private String myTopic;
//    private static final Logger logger = LoggerFactory.getLogger(CYKConsumerService.class);
//

//    @KafkaListener(topics = {"${kafka.topic.my-topic}"}, groupId = "group1")
//    public void consumeMessage2(InstantaneousEntity instantaneousEntity) {
//        instantaneousEntity.getElements();
////        logger.info("消费者消费{}的消息 -> {}", myTopic, instantaneousEntity.toString());
//    }
//}
