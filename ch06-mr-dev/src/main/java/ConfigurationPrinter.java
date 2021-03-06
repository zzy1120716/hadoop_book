import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.Map;

public class ConfigurationPrinter extends Configured implements Tool {

    //确保核心配置外的HDFS、YARN、MapReduce配置能够被获取
    //因为Configuration已经获取了核心配置
    static {
        Configuration.addDefaultResource("hdfs-default.xml");
        Configuration.addDefaultResource("conf/shizhan03/hdfs-site.xml");
        Configuration.addDefaultResource("yarn-default.xml");
        Configuration.addDefaultResource("conf/shizhan03/yarn-site.xml");
        Configuration.addDefaultResource("mapred-default.xml");
        Configuration.addDefaultResource("conf/shizhan03/mapred-site.xml");
    }

    //@Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        for(Map.Entry<String, String> entry : conf) {
            System.out.printf("%s=%s\n", entry.getKey(), entry.getValue());;
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new ConfigurationPrinter(), args);
        System.exit(exitCode);
    }
}
