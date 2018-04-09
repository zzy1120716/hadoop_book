import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

public class URLCatTest {

    private InputStream in = null;
    private String url = "hdfs://localhost:9000/user/zzy/server.log";

    @Before
    public void setUp() throws Exception {
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }

    /*static {
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }*/

    @After
    public void tearDown() throws Exception {
        IOUtils.closeStream(in);
    }

    @Test
    public void urlCat() throws Exception {
        in = new URL(url).openStream();
        IOUtils.copyBytes(in, System.out, 4096, false);
    }

    /*public static void main(String[] args) throws Exception {
        InputStream in = null;
        try {
            in = new URL(args[0]).openStream();
            IOUtils.copyBytes(in, System.out, 4096, false);
        } finally {
            IOUtils.closeStream(in);
        }
    }*/
}
