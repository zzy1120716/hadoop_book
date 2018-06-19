package v2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

//MaxTemperatureDriver的测试，运行在一个mini HDFS和MapReduce集群上
public class MaxTemperatureDriverMiniTest extends ClusterMapReduceTestCase {

    /*
     * 静态嵌套类，实现官方PathFilter接口
     * 过滤掉输出文件中以"_"为开头的文件
     * */
    public static class OutputLogFilter implements PathFilter {
        public boolean accept(Path path) {
            return !path.getName().startsWith("_");
        }
    }

    protected void setUp() throws Exception {
        if(System.getProperty("test.build.data") == null) {
            System.setProperty("test.build.data", "/tmp");
        }
        if(System.getProperty("hadoop.log.dir") == null) {
            System.setProperty("hadoop.log.dir", "/tmp");
        }
        super.setUp();
    }

    //没有标 @Test 注解，是因为ClusterMapReduceTestCase只是一个JUnit 3的测试用例
    public void test() throws Exception {
        Configuration conf = createJobConf();

        Path localInput = new Path("input/ncdc/micro");
        Path input = getInputDir();
        Path output = getOutputDir();

        //复制输入数据到测试HDFS中
        getFileSystem().copyFromLocalFile(localInput, input);

        MaxTemperatureDriver driver = new MaxTemperatureDriver();
        driver.setConf(conf);

        int exitCode = driver.run(new String[] { input.toString(), output.toString() });

        assertThat(exitCode, is(0));

        //检验输出符合预期
        Path[] outputFiles = FileUtil.stat2Paths(getFileSystem().listStatus(output, new OutputLogFilter()));
        assertThat(outputFiles.length, is(1));

        InputStream in = getFileSystem().open(outputFiles[0]);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        assertThat(reader.readLine(), is("1949\t111"));
        assertThat(reader.readLine(), is("1950\t22"));
        assertThat(reader.readLine(), nullValue());
        reader.close();
    }
}
