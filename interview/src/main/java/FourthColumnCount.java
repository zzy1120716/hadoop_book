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

/**
 * @ClassName: FourthColumnCount
 * @description: 面试题1，计算第四列每个元素出现的个数
 * @author: zzy
 * @date: 2019-04-12 11:45
 * @version: V1.0
 **/
public class FourthColumnCount {
    /*
    * input:
    * a,b,c,d
    * b,b,f,e
    * a,a,c,f
    *
    * output:
    * d 1
    * e 1
    * f 1
    * */

    // hdfs dfs -put a.txt /interview/fourthcolumncount/input
    // mvn package -DskipTests
    // hadoop jar interview.jar FourthColumnCount /interview/fourthcolumncount/input /interview/fourthcolumncount/output
    // hdfs dfs -cat /interview/fourthcolumncount/output/part-r-00000

    static class FourthColumnCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] split = line.split(",");
            context.write(new Text(split[3]), new IntWritable(1));
        }
    }

    static class FourthColumnCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "fourth column count");
        job.setJarByClass(FourthColumnCount.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(FourthColumnCountMapper.class);
        job.setCombinerClass(FourthColumnCountReducer.class);
        job.setReducerClass(FourthColumnCountReducer.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
