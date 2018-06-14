package cn.itcast.bigdata.mr.provinceflow;

import cn.itcast.bigdata.mr.flowsum.FlowBean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.HashMap;

/**
 * KEY VALUE 对应的是map输出kv的类型
 */
public class ProvincePartitioner extends Partitioner<Text, FlowBean> {

    public static HashMap<String, Integer> provinceDict = new HashMap<String, Integer>();

    static {
        provinceDict.put("136", 0);
        provinceDict.put("137", 1);
        provinceDict.put("138", 2);
        provinceDict.put("139", 3);
    }

    @Override
    public int getPartition(Text key, FlowBean flowBean, int i) {

//        int bianhao = JdbcUtil.getGuishudiBianhao(key.toString().substring(0, 7))

        String prefix = key.toString().substring(0, 3);
        Integer provinceId = provinceDict.get(prefix);

        return provinceId == null ? 4 : provinceId;
    }

}
