package cn.itcast.bigdata.hdfs;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;

/**
 * 用流的方式来操作hdfs上的文件
 * 可以实现读取指定偏移量范围的数据
 */
public class HdfsStreamAccessTest {

    FileSystem fs = null;
    Configuration conf = null;

    @Before
    public void init() throws Exception {

        Configuration conf = new Configuration();
        //conf.set("fs.defaultFS", "hdfs://mini1:9000");

        // 拿到一个文件系统操作的客户端实例对象
        /*fs = FileSystem.get(conf);*/
        // 可以直接传入uri和用户身份
        fs = FileSystem.get(new URI("hdfs://mini1:9000"), conf, "hadoop");

    }

    /**
     * 通过流的方式上传文件到hdfs
     * @throws Exception
     */
    @Test
    public void testUpload() throws Exception {

        FSDataOutputStream outputStream = fs.create(new Path("/test.py"), true);
        FileInputStream inputStream = new FileInputStream("/Users/zzy/Docs/Leetcode/test.py");

        IOUtils.copy(inputStream, outputStream);

    }

    /**
     * 通过流的方式获取hdfs上的数据
     * @throws Exception
     */
    @Test
    public void testDownload() throws Exception {

        FSDataInputStream inputStream = fs.open(new Path("/test.py"));

        FileOutputStream outputStream = new FileOutputStream("/Users/zzy/Docs/Leetcode/test.py.2");

        IOUtils.copy(inputStream, outputStream);

    }

    @Test
    public void testRandomAccess() throws Exception {

        FSDataInputStream inputStream = fs.open(new Path("/test.py"));

        inputStream.seek(6);

        FileOutputStream outputStream = new FileOutputStream("/Users/zzy/Docs/Leetcode/test.py.part2");

        IOUtils.copy(inputStream, outputStream);

    }

    @Test
    public void testCat() throws Exception {

        FSDataInputStream in = fs.open(new Path("/test.py"));

        //org.apache.hadoop.io.IOUtils.copyBytes(in, System.out, 1024);

        IOUtils.copy(in, System.out);

    }

}
