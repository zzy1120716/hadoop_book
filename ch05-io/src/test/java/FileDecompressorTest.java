import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.apache.hadoop.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class FileDecompressorTest {

    @Test
    @Ignore
    public void decompressesGzippedFile() throws Exception {
        File file = File.createTempFile("file", ".gz");
        file.deleteOnExit();
        InputStream in = this.getClass().getResourceAsStream("/file.gz");
        IOUtils.copyBytes(in, new FileOutputStream(file), 4096, true);

        String path = file.getAbsolutePath();
        FileDecompressor.main(new String[] { path });

        String decompressedPath = path.substring(0, path.length() - 3);
        assertThat(readFile(new File(decompressedPath)), is("Text\n"));
    }

    private String readFile(File file) throws IOException {
        return new Scanner(file).useDelimiter("\\A").next();
    }
}
