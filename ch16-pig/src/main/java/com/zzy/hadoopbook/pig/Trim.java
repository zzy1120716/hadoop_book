package com.zzy.hadoopbook.pig;

import org.apache.pig.PrimitiveEvalFunc;

import java.io.IOException;

public class Trim extends PrimitiveEvalFunc<String, String> {

    @Override
    public String exec(String input) throws IOException {
        return input.trim();
    }
}
