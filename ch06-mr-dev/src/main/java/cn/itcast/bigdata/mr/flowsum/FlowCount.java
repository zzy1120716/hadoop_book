package cn.itcast.bigdata.mr.flowsum;

import cn.itcast.bigdata.mr.provinceflow.ProvincePartitioner;
import cn.itcast.bigdata.mr.wcdemo.WordcountDriver;
import cn.itcast.bigdata.mr.wcdemo.WordcountMapper;
import cn.itcast.bigdata.mr.wcdemo.WordcountReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FlowCount {

    static class FlowCountMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            // 将一行内容转成string
            String line = value.toString();
            // 切分字段
            String[] fields = line.split("\t");
            // 取出手机号
            String phoneNbr = fields[1];
            // 取出上行流量、下行流量
            long upFlow = Long.parseLong(fields[fields.length - 3]);
            long dFlow = Long.parseLong(fields[fields.length - 2]);

            context.write(new Text(phoneNbr), new FlowBean(upFlow, dFlow));

        }
    }

    static class FlowCountReducer extends Reducer<Text, FlowBean, Text, FlowBean> {

        // TreeMap<FlowBean, Text> treeMap = new TreeMap<FlowBean, Text>();

        // <13833333,bean1><13833333,bean2><13833333,bean3><13833333,bean4>......
        @Override
        protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {

            long sum_upFlow = 0;
            long sum_dFlow = 0;

            // 遍历所有bean，将其中的上行流量，下行流量分别累加
            for (FlowBean bean : values) {
                sum_upFlow += bean.getUpFlow();
                sum_dFlow += bean.getdFlow();
            }

            FlowBean resultBean = new FlowBean(sum_upFlow, sum_dFlow);

            /*treeMap.put(resultBean, key);
            Set<Map.Entry<FlowBean, Text>> entrySet = treeMap.entrySet();
            for (Map.Entry<FlowBean, Text> ent : entrySet) {
                context.write(ent.getValue(), ent.getKey());
            }*/

            context.write(key, resultBean);

        }

        /*@Override
        protected void cleanup(Context context) throws IOException, InterruptedException {

        }*/
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        /*conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.hostname", "mini1");*/
        Job job = Job.getInstance(conf);

        /*job.setJar("/home/hadoop/wc.jar");*/

        // 指定本程序的jar包所在的本地路径
        job.setJarByClass(FlowCount.class);

        // 指定本业务job要使用的mapper、reducer业务类
        job.setMapperClass(FlowCountMapper.class);
        job.setReducerClass(FlowCountReducer.class);

        // 指定mapper输出数据的kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        // 指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // 指定job的输入原始文件所在目录
        FileInputFormat.setInputPaths(job, new Path(args[0]));

        // 指定job的输出结果所在目录
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        /*job.submit();*/
        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);

    }

}
