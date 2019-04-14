package secondarysort;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/*
input:
b,10
b,128
z,111
z,250
a,1
b,12
c,77
z,33
a,233
c,666
* */

/*
output:
a	1
a	233
b	10
b	12
b	128
c	77
c	666
z	33
z	111
z	250
* */

// hadoop jar interview.jar secondarysort.SecondarySortMapReduce
// hadoop fs -cat /interview/secondarysort/output/part-r-00000
public class SecondarySortMapReduce extends Configured implements Tool {

    // step 1 : Mapper Class
    public static class SortMapper extends
            Mapper<LongWritable, Text, PairWritable, IntWritable> {
        private PairWritable mapOutputKey = new PairWritable();
        private IntWritable mapOutputValue = new IntWritable();

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            // line value
            String lineValue = value.toString();
            // split
            String[] strs = lineValue.split(",");
            // invalidate
            if (2 != strs.length) {
                return;
            }
            // set map output key
            mapOutputKey.set(strs[0], Integer.valueOf(strs[1]));
            mapOutputValue.set(Integer.valueOf(strs[1]));
            // output
            context.write(mapOutputKey, mapOutputValue);
        }
    }

    // step 2 : Reducer Class
    public static class SortReducer extends
            Reducer<PairWritable, IntWritable, Text, IntWritable> {
        private Text outputKey = new Text();

        @Override
        protected void reduce(PairWritable key, Iterable<IntWritable> values,
                              Context context) throws IOException, InterruptedException {
            // iterator
            for (IntWritable value : values) {
                outputKey.set(key.getFirst());
                context.write(outputKey, value);
            }
        }
    }

    /**
     *
     * @param args
     * @return
     * @throws Exception
     *             int run(String [] args) throws Exception;
     */
    // step 3 : Driver
    public int run(String[] args) throws Exception {
        Configuration configuration = this.getConf();

        // set job
        Job job = Job.getInstance(configuration, this.getClass().getSimpleName());
        job.setJarByClass(SecondarySortMapReduce.class);

        // input
        Path inpath = new Path(args[0]);
        FileInputFormat.addInputPath(job, inpath);

        // output
        FileSystem fileSystem = FileSystem.get(new URI(args[1]), configuration);
        // 如果输出目录存在，我们就删除
        if (fileSystem.exists(new Path(args[1]))) {
            fileSystem.delete(new Path(args[1]), true);
        }
        Path outpath = new Path(args[1]);
        FileOutputFormat.setOutputPath(job, outpath);

        // Mapper
        job.setMapperClass(SortMapper.class);
        job.setMapOutputKeyClass(PairWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        // partitioner
        job.setPartitionerClass(FirstPartitioner.class);

        // group
        job.setGroupingComparatorClass(FirstGroupingComparator.class);

        // Reducer
        job.setReducerClass(SortReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // reduce number
        // job.setNumReduceTasks(2);
        // submit job
        boolean isSuccess = job.waitForCompletion(true);
        return isSuccess ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
        args = new String[] {
                "hdfs://localhost:9000/interview/secondarysort/input",
                "hdfs://localhost:9000/interview/secondarysort/output" };
        // run job
        int status = ToolRunner.run(configuration,
                new SecondarySortMapReduce(), args);

        // exit program
        System.exit(status);
    }

}
