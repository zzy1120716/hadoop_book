package join;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
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
customers.csv
1,Stephanie Leung,555-555-5555
2,Edward Kim,123-456-7890
3,Jose Madriz,281-330-8004
4,David Stork,408-555-0000

orders.csv
3,A,12.95,02-Jun-2008
1,B,88.25,20-May-2008
2,C,32.00,30-Nov-2007
3,D,25.02,22-Jan-2009
* */

/*
output:
1,Stephanie Leung,555-555-5555,B,88.25,20-May-2008
2,Edward Kim,123-456-7890,C,32.00,30-Nov-2007
3,Jose Madriz,281-330-8004,D,25.02,22-Jan-2009
3,Jose Madriz,281-330-8004,A,12.95,02-Jun-2008
* */

// hadoop jar interview.jar join.DataJoinMapReduce
// hadoop fs -cat /interview/join/output/part-r-00000
public class DataJoinMapReduce extends Configured implements Tool {

    // step 1 : Mapper Class

    public static class DataJoinMapper extends
            Mapper<LongWritable, Text, LongWritable, DataJoinWritable> {

        // map output key
        private LongWritable mapOutputKey = new LongWritable();

        // map output value
        private DataJoinWritable mapOutputValue = new DataJoinWritable();

        @Override
        protected void setup(Context context) throws IOException,
                InterruptedException {
        }

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            // line value
            String linevalue = value.toString();

            // split
            String[] vals = linevalue.split(",");

            int length = vals.length;
            if ((length != 3) && (length != 4)) {
                return;
            }

            // get cid
            long cid = Long.valueOf(vals[0]);

            // get name
            String name = vals[1];

            // set customer
            if (3 == length) {
                String phone = vals[2];
                mapOutputKey.set(cid);
                mapOutputValue.set("customer", name + "," + phone);
            }

            // set order
            if (4 == length) {
                String price = vals[2];
                String date = vals[3];
                mapOutputKey.set(cid);
                mapOutputValue.set("order", name + "," + price + "," + date);
            }
            //output
            context.write(mapOutputKey, mapOutputValue);
        }

        @Override
        protected void cleanup(Context context) throws IOException,
                InterruptedException {
        }
    }

    // step 2 : Reducer Class
    public static class DataJoinReducer extends
            Reducer<LongWritable, DataJoinWritable, NullWritable, Text> {

        private Text outputValue = new Text();

        @Override
        protected void setup(Context context) throws IOException,
                InterruptedException {
        }

        @Override
        protected void reduce(LongWritable key,
                              Iterable<DataJoinWritable> values, Context context)
                throws IOException, InterruptedException {
            String customerInfo = null;
            List<String> orderList = new ArrayList<String>();

            // iterator
            for (DataJoinWritable value : values) {
                if ("customer".equals(value.getTag())) {
                    customerInfo = value.getData();
                } else if ("order".equals(value.getTag())) {
                    orderList.add(value.getData());
                }
            }

            // output
            for (String order : orderList) {
                // set output value
                outputValue.set(key.get() + "," + customerInfo + "," + order);
                // output
                context.write(NullWritable.get(), outputValue);
            }

        }

        @Override
        protected void cleanup(Context context) throws IOException,
                InterruptedException {
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

        Job job = Job.getInstance(configuration, this.getClass()
                .getSimpleName());
        job.setJarByClass(DataJoinMapReduce.class);

        // set job
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
        job.setMapperClass(DataJoinMapper.class);
        // TODD
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(DataJoinWritable.class);

        // Reducer
        job.setReducerClass(DataJoinReducer.class);
        // TODD
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        // submit job
        boolean isSuccess = job.waitForCompletion(true);

        return isSuccess ? 0 : 1;

    }

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
        // 传递两个参数，设置路径
        args = new String[] {
                "hdfs://localhost:9000/interview/join/input",
                "hdfs://localhost:9000/interview/join/output" };


        // run job
        int status = ToolRunner.run(configuration, new DataJoinMapReduce(),
                args);

        // exit program
        System.exit(status);
    }

}
