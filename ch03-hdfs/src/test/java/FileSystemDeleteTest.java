import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.is;

public class FileSystemDeleteTest {

    private FileSystem fs;

    @Before
    public void setUp() throws Exception {
        fs = FileSystem.get(new Configuration());
        writeFile(fs, new Path("dir/file"));
    }

    private void writeFile(FileSystem fileSys, Path name) throws Exception {
        FSDataOutputStream stm = fileSys.create(name);
        stm.close();
    }

    @Test
    public void deleteFile() throws Exception {
        assertThat(fs.delete(new Path("dir/file"), false), is(true));   //删除文件，非递归删除
        assertThat(fs.exists(new Path("dir/file")), is(false)); //验证文件是否已删除（是）
        assertThat(fs.exists(new Path("dir")), is(true)); //验证目录是否已删除（否）
        assertThat(fs.delete(new Path("dir"), false), is(true));    //删除目录，此时已是空目录，故非递归删除
        assertThat(fs.exists(new Path("dir")), is(false));  //验证目录是否已删除（是）
    }

    @Test
    public void deleteNonEmptyDirectoryNonRecursivelyFails() throws Exception {
        try {
            fs.delete(new Path("dir"), false);
            fail("Shouldn't delete non-empty directory");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void deleteDirectory() throws Exception {
        assertThat(fs.delete(new Path("dir"), true), is(true)); //递归删除dir
        assertThat(fs.exists(new Path("dir")), is(false));   //删除成功
    }
}
