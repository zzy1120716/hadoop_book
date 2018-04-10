import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.apache.hadoop.io.NullWritable;
import org.junit.Test;

import java.io.IOException;

public class NullWritableTest extends WritableTestBase {

    @Test
    public void test() throws IOException {
        NullWritable writable = NullWritable.get();
        assertThat(serialize(writable).length, is(0));
    }
}
