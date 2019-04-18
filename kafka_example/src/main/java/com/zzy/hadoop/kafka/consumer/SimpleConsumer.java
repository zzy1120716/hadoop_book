package com.zzy.hadoop.kafka.consumer;

import com.google.gson.Gson;
import com.zzy.hadoop.kafka.common.MessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SimpleConsumer
 * @description: TODO
 * @author: zzy
 * @date: 2019-04-18 11:08
 * @version: V1.0
 **/
@Slf4j
@Component
public class SimpleConsumer {

    private final Gson gson = new Gson();

    @KafkaListener(topics = "${kafka.topic.default}", containerFactory = "kafkaListenerContainerFactory")
    public void receive(MessageEntity message) {
        log.info(gson.toJson(message));
    }
}