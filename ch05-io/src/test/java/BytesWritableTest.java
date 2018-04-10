import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.util.StringUtils;
import org.junit.Test;

import java.io.IOException;

public class BytesWritableTest extends WritableTestBase {

    @Test
    public void test() throws IOException {
        BytesWritable b = new BytesWritable(new byte[] { 3, 5 });
        byte[] bytes =  serialize(b);
        assertThat(StringUtils.byteToHexString(bytes), is("000000020305"));
        //设置长度
        b.setCapacity(11);
        assertThat(b.getLength(), is(2));
        assertThat(b.getBytes().length, is(11));
    }
}
