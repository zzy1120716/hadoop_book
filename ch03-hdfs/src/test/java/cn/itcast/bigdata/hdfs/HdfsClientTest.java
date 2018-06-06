package cn.itcast.bigdata.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

/**
 * 客户端去操作hdfs时，是有一个用户身份的
 * 默认情况下，hdfs客户端api会从jvm中获取一个参数来作为自己的用户身份，-DHADOOP_USER_NAME=hadoop
 */
public class HdfsClientTest {

    private FileSystem fs = null;

    @Before
    public void init() throws Exception {

        Configuration conf = new Configuration();
        //conf.set("fs.defaultFS", "hdfs://mini1:9000");

        // 拿到一个文件系统操作的客户端实例对象
        /*fs = FileSystem.get(conf);*/
        // 可以直接传入uri和用户身份
        fs = FileSystem.get(new URI("hdfs://mini1:9000"), conf, "hadoop");
    }

    @Test
    public void testDownload() throws Exception {

        fs.copyToLocalFile(new Path("/test.py.copy"), new Path("/Users/zzy/Docs/LeetCode"));
        fs.close();
    }

    @Test
    public void testUpload() throws Exception {

        //Thread.sleep(5000000);
        fs.copyFromLocalFile(new Path("/Users/zzy/Docs/LeetCode/test.py"), new Path("/test.py.copy"));
        fs.close();

    }

}
