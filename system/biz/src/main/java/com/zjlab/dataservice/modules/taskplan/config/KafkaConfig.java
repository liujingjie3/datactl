//package com.zjlab.dataservice.modules.taskplan.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.support.converter.RecordMessageConverter;
//import org.springframework.kafka.support.converter.StringJsonMessageConverter;
//
///**
// * @author shuang.kou
// */
//@Configuration
//public class KafkaConfig {
//
//    @Value("${kafka.topic.my-topic}")
//    String cykTopic;
//
//    /**
//     * JSON消息转换器
//     */
//    @Bean
//    public RecordMessageConverter jsonConverter() {
//        return new StringJsonMessageConverter();
//    }
//
//    /**
//     * 通过注入一个 NewTopic 类型的 Bean 来创建 topic，如果 topic 已存在，则会忽略。
//     */
//    @Bean
//    public NewTopic myTopic() {
//        return new NewTopic(cykTopic, 2, (short) 1);
//    }
//
//}
