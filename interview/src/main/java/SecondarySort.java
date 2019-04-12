import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueLineRecordReader;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;

/**
 * @ClassName: SecondarySort
 * @description: 面试题2，实现二次排序
 * @author: zzy
 * @date: 2019-04-12 14:20
 * @version: V1.0
 **/
public class SecondarySort {

    /*
    * input:
    sort1   1
    sort2   3
    sort2   88
    sort2   54
    sort1   2
    sort6   22
    sort6   888
    sort6   58
    *
    * output:
    * sort1	    1,2
    * sort2	    3,54,88
    * sort6	    22,58,888
    * */

    // hadoop jar interview.jar SecondarySort /interview/secondarysort/input /interview/secondarysort/output

    /**
     * 自定义组合键
     */
    static class CombinationKey implements WritableComparable<CombinationKey> {
        private Text firstKey;
        private IntWritable secondKey;

        public CombinationKey() {
            this.firstKey = new Text();
            this.secondKey = new IntWritable();
        }

        public Text getFirstKey() {
            return firstKey;
        }

        public void setFirstKey(Text firstKey) {
            this.firstKey = firstKey;
        }

        public IntWritable getSecondKey() {
            return secondKey;
        }

        public void setSecondKey(IntWritable secondKey) {
            this.secondKey = secondKey;
        }

        @Override
        public void write(DataOutput dataOutput) throws IOException {
            this.firstKey.write(dataOutput);
            this.secondKey.write(dataOutput);
        }

        @Override
        public void readFields(DataInput dataInput) throws IOException {
            this.firstKey.readFields(dataInput);
            this.secondKey.readFields(dataInput);
        }

        /**
         * 自定义比较策略
         * 注意：该比较策略用于MapReduce的第一次默认排序
         * 也就是发生在Map端的sort阶段
         * 发生地点为环形缓冲区(可以通过io.sort.mb进行大小调整)
         */
        @Override
        public int compareTo(CombinationKey combinationKey) {
            System.out.println("------------------------CombineKey flag-------------------");
            return this.firstKey.compareTo(combinationKey.getFirstKey());
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((firstKey == null) ? 0 : firstKey.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CombinationKey other = (CombinationKey) obj;
            if (firstKey == null) {
                if (other.firstKey != null)
                    return false;
            } else if (!firstKey.equals(other.firstKey))
                return false;
            return true;
        }
    }

    /**
     * 自定义分区器
     */
    static class DefinedPartition extends Partitioner<CombinationKey, IntWritable> {

        /**
         * 数据输入来源：map输出 我们这里根据组合键的第一个值作为分区
         * 如果不自定义分区的话，MapReduce会根据默认的Hash分区方法
         * 将整个组合键相等的分到一个分区中，这样的话显然不是我们要的效果
         * @param key map输出键值
         * @param value map输出value值
         * @param numPartitions 分区总数，即reduce task个数
         */
        @Override
        public int getPartition(CombinationKey key, IntWritable value, int numPartitions) {
            System.out.println("---------------------进入自定义分区---------------------");
            System.out.println("---------------------结束自定义分区---------------------");
            return (key.getFirstKey().hashCode() & Integer.MAX_VALUE) % numPartitions;
        }
    }

    /**
     * 自定义比较器
     */
    static class DefinedComparator extends WritableComparator {

        protected DefinedComparator() {
            super(CombinationKey.class, true);
        }

        /**
         * 第一列按升序排列，第二列也按升序排列
         */
        public int compare(WritableComparable a, WritableComparable b) {
            System.out.println("---------------------进入二次排序---------------------");
            CombinationKey c1 = (CombinationKey) a;
            CombinationKey c2 = (CombinationKey) b;
            int minus = c1.getFirstKey().compareTo(c2.getFirstKey());

            if (minus != 0) {
                System.out.println("---------------------结束二次排序---------------------");
                return minus;
            } else {
                System.out.println("---------------------结束二次排序---------------------");
                return c1.getSecondKey().get() - c2.getSecondKey().get();
            }
        }
    }

    /**
     * 自定义分组有两种方式，一种是继承WritableComparator
     * 另外一种是实现RawComparator接口
     */
    static class DefinedGroupSort extends WritableComparator {

        protected DefinedGroupSort() {
            super(CombinationKey.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            System.out.println("---------------------进入自定义分组---------------------");
            CombinationKey combinationKey1 = (CombinationKey) a;
            CombinationKey combinationKey2 = (CombinationKey) b;
            System.out.println("---------------------分组结果：" + combinationKey1.getFirstKey().compareTo(combinationKey2.getFirstKey()));
            System.out.println("---------------------结束自定义分组---------------------");
            //自定义按原始数据中第一个key分组
            return combinationKey1.getFirstKey().compareTo(combinationKey2.getFirstKey());
        }
    }

    static class SecondarySortMapper extends Mapper<Text, Text, CombinationKey, IntWritable> {
        /**
         * 这里要特殊说明一下，为什么要将这些变量写在map函数外边
         * 对于分布式的程序，我们一定要注意到内存的使用情况，对于MapReduce框架
         * 每一行的原始记录的处理都要调用一次map()函数，假设，这个map()函数要处理1一亿
         * 条输入记录，如果将这些变量都定义在map函数里面则会导致这4个变量的对象句柄
         * 非常的多(极端情况下将产生4*1亿个句柄，当然java也是有自动的GC机制的，一定不会达到这么多)
         * 导致栈内存被浪费掉，我们将其写在map函数外面，顶多就只有4个对象句柄
         */
        private CombinationKey combinationKey = new CombinationKey();
        Text sortName = new Text();
        IntWritable score = new IntWritable();
        String[] splits = null;
        protected void map(Text key, Text value, Mapper<Text, Text, CombinationKey, IntWritable>.Context context) throws IOException, InterruptedException {
            System.out.println("---------------------进入map()函数---------------------");
            // 过滤非法记录(这里用计数器比较好)
            if (key == null || value == null || key.toString().equals("")){
                return;
            }
            // 构造相关属性
            sortName.set(key.toString());
            score.set(Integer.parseInt(value.toString()));
            // 设置联合key
            combinationKey.setFirstKey(sortName);
            combinationKey.setSecondKey(score);

            // 通过context把map处理后的结果输出
            context.write(combinationKey, score);
            System.out.println("---------------------结束map()函数---------------------");
        }
    }

    static class SecondarySortReducer extends Reducer<CombinationKey, IntWritable, Text, Text> {

        StringBuffer sb = new StringBuffer();
        Text score = new Text();
        /**
         * 这里要注意一下reduce的调用时机和次数：
         * reduce每次处理一个分组的时候会调用一次reduce函数。
         * 所谓的分组就是将相同的key对应的value放在一个集合中
         * 例如：<sort1,1> <sort1,2>
         * 分组后的结果就是
         * <sort1,{1,2}>这个分组会调用一次reduce函数
         */
        protected void reduce(CombinationKey key, Iterable<IntWritable> values, Reducer<CombinationKey, IntWritable, Text, Text>.Context context)
                throws IOException, InterruptedException {


            // 先清除上一个组的数据
            sb.delete(0, sb.length());

            for (IntWritable val : values){
                sb.append(val.get() + ",");
            }

            // 取出最后一个逗号
            if (sb.length() > 0){
                sb.deleteCharAt(sb.length() - 1);
            }

            //设置写出去的value
            score.set(sb.toString());

            //将联合Key的第一个元素作为新的key，将score作为value写出去
            context.write(key.getFirstKey(), score);

            System.out.println("---------------------进入reduce()函数---------------------");
            System.out.println("---------------------{[" + key.getFirstKey()+"," + key.getSecondKey() + "],[" +score +"]}");
            System.out.println("---------------------结束reduce()函数---------------------");
        }
    }

    public static void main(String[] args) throws Exception {

        // 创建配置信息
        Configuration conf = new Configuration();
        conf.set(KeyValueLineRecordReader.KEY_VALUE_SEPERATOR, "\t");

        // 创建文件系统
        FileSystem fileSystem = FileSystem.get(new URI(args[1]), conf);
        // 如果输出目录存在，我们就删除
        if (fileSystem.exists(new Path(args[1]))) {
            fileSystem.delete(new Path(args[1]), true);
        }

        // 创建任务
        Job job = Job.getInstance(conf, "secondary sort");
        job.setJarByClass(FourthColumnCount.class);

        //1.1	设置输入目录和设置输入数据格式化的类
        FileInputFormat.setInputPaths(job, args[0]);
        job.setInputFormatClass(KeyValueTextInputFormat.class);

        //1.2	设置自定义Mapper类和设置map函数输出数据的key和value的类型
        job.setMapperClass(SecondarySortMapper.class);
        job.setMapOutputKeyClass(CombinationKey.class);
        job.setMapOutputValueClass(IntWritable.class);

        //1.3	设置分区和reduce数量(reduce的数量，和分区的数量对应，因为分区为一个，所以reduce的数量也是一个)
        job.setPartitionerClass(DefinedPartition.class);
        job.setNumReduceTasks(1);

        //设置自定义分组策略
        job.setGroupingComparatorClass(DefinedGroupSort.class);
        //设置自定义比较策略(因为我的CombineKey重写了compareTo方法，所以这个可以省略)
        job.setSortComparatorClass(DefinedComparator.class);

        //1.4	排序
        //1.5	归约
        //2.1	Shuffle把数据从Map端拷贝到Reduce端。
        //2.2	指定Reducer类和输出key和value的类型
        job.setReducerClass(SecondarySortReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //2.3	指定输出的路径和设置输出的格式化类
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputFormatClass(TextOutputFormat.class);

        // 提交作业，退出
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
