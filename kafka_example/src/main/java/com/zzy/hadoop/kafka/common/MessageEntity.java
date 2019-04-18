package com.zzy.hadoop.kafka.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: MessageEntity
 * @description: TODO
 * @author: zzy
 * @date: 2019-04-18 11:04
 * @version: V1.0
 **/
@Getter
@Setter
@EqualsAndHashCode
public class MessageEntity {
    private String title;
    private String body;

    @Override
    public String toString() {
        return "MessageEntity{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}