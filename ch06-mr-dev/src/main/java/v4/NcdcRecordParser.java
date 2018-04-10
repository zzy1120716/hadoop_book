package v4;

import org.apache.hadoop.io.Text;

public class NcdcRecordParser {

    private static final int MISSING_TEMPERATURE = 9999;

    private String year;
    private int airTemperature;
    private boolean airTemperatureMalformed;
    private String quality;

    public void parse(String record) {
        year = record.substring(15, 19);
        airTemperatureMalformed = false;
        //去除起始的加号，从而使用parseInt(pre-Java 7)
        if(record.charAt(87) == '+') {
            airTemperature = Integer.parseInt(record.substring(88, 92));
        } else if(record.charAt(87) == '-') {
            airTemperature = Integer.parseInt(record.substring(87, 92));
        } else {
            //符号位不合法
            airTemperatureMalformed = true;
        }
        //温度的后一位表示数据是否合法
        quality = record.substring(92, 93);
    }

    public void parse(Text record) {
        parse(record.toString());
    }

    public boolean isValidTemperature() {
        //quality值为0,1,4,5,9时，温度合法
        return ! airTemperatureMalformed && airTemperature != MISSING_TEMPERATURE && quality.matches("[01459]");
    }

    public boolean isMalformedTemperature() {
        return airTemperatureMalformed;
    }

    public String getYear() {
        return year;
    }

    public int getAirTemperature() {
        return airTemperature;
    }
}
