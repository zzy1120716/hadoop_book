package cn.itcast.bigdata.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Iterator;
import java.util.Map.Entry;

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

    @Test
    public void testConf() {

        Configuration conf = new Configuration();
        Iterator<Entry<String, String>> it = conf.iterator();
        while (it.hasNext()) {
            Entry<String, String> ent = it.next();
            System.out.println(ent.getKey() + " : " + ent.getValue());
        }
    }

    @Test
    public void testMkdir() throws Exception {

        boolean mkdirs = fs.mkdirs(new Path("/testmkdir/aaa/bbb"));
        System.out.println(mkdirs);
    }

    @Test
    public void testDelete() throws Exception {

        boolean flag = fs.delete(new Path("/testmkdir/aaa"), true);
        System.out.println(flag);
    }

    /**
     * 递归列出指定目录下的所有子文件夹中的文件
     * @throws Exception
     */
    @Test
    public void testLs() throws Exception {

        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);

        while (listFiles.hasNext()) {

            LocatedFileStatus fileStatus = listFiles.next();
            System.out.println("owner: " + fileStatus.getOwner());
            System.out.println("blocksize: " + fileStatus.getBlockSize());
            System.out.println("replication: " + fileStatus.getReplication());
            System.out.println("permission: " + fileStatus.getPermission());
            System.out.println("name: " + fileStatus.getPath().getName());
            System.out.println("------------------------------");

            BlockLocation[] blockLocations = fileStatus.getBlockLocations();

            for (BlockLocation b : blockLocations) {
                //System.out.println("块的名称：" + b.getNames());
                System.out.println("块起始偏移量：" + b.getOffset());
                System.out.println("块长度：" + b.getLength());
                String[] datanodes = b.getHosts();
                for (String dn : datanodes) {
                    System.out.println("datanode：" + dn);
                }
            }

        }

    }

    @Test
    public void testLs2() throws Exception {

        FileStatus[] listStatus = fs.listStatus(new Path("/"));
        for (FileStatus file : listStatus) {
            System.out.println("name: " + file.getPath().getName());
            System.out.println(file.isFile() ? "file" : "directory");
        }
    }

}
