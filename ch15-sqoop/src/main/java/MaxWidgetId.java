import org.apache.hadoop.conf.Configured;
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
import org.apache.sqoop.lib.RecordParser.ParseError;

import java.io.IOException;

public class MaxWidgetId extends Configured implements Tool {

    public static class MaxWidgetMapper extends Mapper<LongWritable, Text, LongWritable, Widget> {

        private Widget maxWidget = null;

        @Override
        public void map(LongWritable k, Text v, Context context) {
            Widget widget = new Widget();
            try {
                widget.parse(v);    // Auto-generated: parse all fields from text.
            } catch (ParseError pe) {
                // Got a malformed record. Ignore it.
                return;
            }

            Integer id = widget.get_id();
            if(null != id) {
                if(maxWidget == null || id > maxWidget.get_id()) {
                    maxWidget = widget;
                }
            }
        }

        @Override
        public void cleanup(Context context) throws IOException, InterruptedException {
            if(null != maxWidget) {
                context.write(new LongWritable(0), maxWidget);
            }
        }
    }

    public static class MaxWidgetReducer extends Reducer<LongWritable, Widget, Widget, NullWritable> {

        // There will be a single reduce call with key '0' which gets
        // the max widget from each map task. Pick the max widget from
        // this list.

        @Override
        public void reduce(LongWritable k, Iterable<Widget> vals, Context context) throws IOException, InterruptedException {
            Widget maxWidget = null;

            for(Widget w : vals) {
                if(maxWidget == null || w.get_id() > maxWidget.get_id()) {
                    try {
                        maxWidget = (Widget) w.clone();
                    } catch (CloneNotSupportedException cnse) {
                        // Shouldn't happen; Sqoop-generated classes support clone().
                        throw new IOException(cnse);
                    }
                }
            }

            if(null != maxWidget) {
                context.write(maxWidget, NullWritable.get());
            }
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = new Job(getConf());

        job.setJarByClass(MaxWidgetId.class);

        job.setMapperClass(MaxWidgetMapper.class);
        job.setReducerClass(MaxWidgetReducer.class);

        FileInputFormat.addInputPath(job, new Path("widgets"));
        FileOutputFormat.setOutputPath(job, new Path("maxwidget"));

        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(Widget.class);

        job.setOutputKeyClass(Widget.class);
        job.setOutputValueClass(NullWritable.class);

        job.setNumReduceTasks(1);

        if(!job.waitForCompletion(true)) {
            return 1;   // error.
        }

        return 0;
    }

    public static void main(String[] args) throws Exception {
        int ret = ToolRunner.run(new MaxWidgetId(), args);
        System.exit(ret);
    }
}
