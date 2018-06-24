package cn.itcast.bigdata.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.HashMap;

public class ToLowerCase extends UDF {

    private static HashMap<String, String> province = new HashMap<>();

    static {
        province.put("136", "beijing");
        province.put("137", "shanghai");
        province.put("138", "shenzhen");
    }

    // 必须是public
    public String evaluate(String field) {

        String result = field.toLowerCase();

        return result;
    }

    public String evaluate(int phoneNbr) {

        String phoneStr = String.valueOf(phoneNbr);
        return province.get(phoneStr.substring(0, 3)) == null ? "huoxing" : province.get(phoneStr.substring(0, 3));
    }

}
