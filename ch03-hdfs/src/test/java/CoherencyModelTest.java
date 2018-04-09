import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CoherencyModelTest {

    private MiniDFSCluster cluster; //使用进程临时创建的HDFS集群做测试
    private FileSystem fs;

    @Before
    public void setUp() throws IOException {
        Configuration conf = new Configuration();
        if (System.getProperty("test.build.data") == null) {
            System.setProperty("test.build.data", "/tmp");
        }
        cluster = new MiniDFSCluster.Builder(conf).build();
        fs = cluster.getFileSystem();
    }

    @After
    public void tearDown() throws Exception {
        fs.close();
        cluster.shutdown();
    }

    @Test
    public void fileExistsImmediatelyAfterCreation() throws Exception {
        //创建后文件立即存在
        Path p = new Path("p");
        fs.create(p);
        assertThat(fs.exists(p), is(true));
        assertThat(fs.delete(p, true), is(true));
    }

    @Test
    public void fileContentIsNotVisibleAfterFlush() throws Exception {
        //测试flush()后，内容是否可见
        Path p = new Path("p");
        OutputStream out = fs.create(p);
        out.write("content".getBytes("UTF-8"));
        /*[*/ out.flush(); /*]*/
        assertThat(fs.getFileStatus(p).getLen(), is(0L));
        out.close();
        assertThat(fs.delete(p, true), is(true));
    }

    @Test
    public void fileContentIsVisibleAfterHFlush() throws Exception {
        //测试hlush()后，内容是否可见
        Path p = new Path("p");
        FSDataOutputStream out = fs.create(p);
        out.write("content".getBytes("UTF-8"));
        /*[*/ out.hflush(); /*]*/
        assertThat(fs.getFileStatus(p).getLen(), is((long) "content".length()));
        out.close();
        assertThat(fs.delete(p, true), is(true));
    }

    @Test
    public void fileContentIsVisibleAfterHSync() throws Exception {
        //测试hsync()后，内容是否可见
        Path p = new Path("p");
        FSDataOutputStream out = fs.create(p);
        out.write("content".getBytes("UTF-8"));
        /*[*/ out.hsync(); /*]*/
        assertThat(fs.getFileStatus(p).getLen(), is((long) "content".length()));
        out.close();
        assertThat(fs.delete(p, true), is(true));
    }

    @Test
    public void localFileContentIsVisibleAfterFlushAndSync() throws Exception {
        File localFile = File.createTempFile("tmp", "");
        assertThat(localFile.exists(), is(true));
        FileOutputStream out = new FileOutputStream(localFile);
        out.write("content".getBytes("UTF-8"));
        out.flush();    //flush到操作系统中
        out.getFD().sync(); //sync到磁盘
        assertThat(localFile.length(), is((long) "content".length()));
        out.close();
        assertThat(localFile.delete(), is(true));
    }

    @Test
    public void fileContentIsVisibleAfterClose() throws Exception {
        Path p = new Path("p");
        OutputStream out = fs.create(p);
        out.write("content".getBytes("UTF-8"));
        /*[*/ out.close(); /*]*/
        assertThat(fs.getFileStatus(p).getLen(), is((long) "content".length()));
        assertThat(fs.delete(p, true), is(true));
    }
}






























