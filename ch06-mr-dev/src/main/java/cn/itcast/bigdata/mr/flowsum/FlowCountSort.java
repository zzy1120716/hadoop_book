package cn.itcast.bigdata.mr.flowsum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FlowCountSort {

    static class FlowCountSortMapper extends Mapper<LongWritable, Text, FlowBean, Text> {

        FlowBean bean = new FlowBean();
        Text v = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            // 拿到的是上一个统计程序的输出结果，已经是各手机号的总流量信息
            String line = value.toString();

            String[] fields = line.split("\t");

            String phoneNbr = fields[0];

            long upFlow = Long.parseLong(fields[1]);
            long dFlow = Long.parseLong(fields[2]);

            bean.set(upFlow, dFlow);
            v.set(phoneNbr);

            context.write(bean, v);

        }
    }

    static class FlowCountSortReducer extends Reducer<FlowBean, Text, Text, FlowBean> {

        // <bean(),phoneNbr>
        @Override
        protected void reduce(FlowBean bean, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            context.write(values.iterator().next(), bean);

        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        /*conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.hostname", "mini1");*/
        Job job = Job.getInstance(conf);

        /*job.setJar("/home/hadoop/wc.jar");*/

        // 指定本程序的jar包所在的本地路径
        job.setJarByClass(FlowCountSort.class);

        // 指定本业务job要使用的mapper、reducer业务类
        job.setMapperClass(FlowCountSortMapper.class);
        job.setReducerClass(FlowCountSortReducer.class);

        // 指定mapper输出数据的kv类型
        job.setMapOutputKeyClass(FlowBean.class);
        job.setMapOutputValueClass(Text.class);

        // 指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // 指定job的输入原始文件所在目录
        FileInputFormat.setInputPaths(job, new Path(args[0]));

        Path outPath = new Path(args[1]);
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(outPath)) {
            fs.delete(outPath, true);
        }

        // 指定job的输出结果所在目录
        FileOutputFormat.setOutputPath(job, outPath);

        // 将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        /*job.submit();*/
        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);

    }

}
