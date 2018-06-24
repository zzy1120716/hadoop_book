package cn.itcast.bigdata.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonParser extends UDF {

    public String evaluate(String jsonLine) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MovieRateBean bean = objectMapper.readValue(jsonLine, MovieRateBean.class);
            return bean.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
