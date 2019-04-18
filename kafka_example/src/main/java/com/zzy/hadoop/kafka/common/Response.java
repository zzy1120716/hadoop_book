package com.zzy.hadoop.kafka.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: Response
 * @description: TODO
 * @author: zzy
 * @date: 2019-04-18 11:04
 * @version: V1.0
 **/
@Getter
@Setter
public class Response {
    private int code;
    private String message;

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }
}