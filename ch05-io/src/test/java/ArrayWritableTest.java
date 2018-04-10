import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableUtils;
import org.junit.Test;

import java.io.IOException;

public class ArrayWritableTest {

    @Test
    public void test() throws IOException {
        ArrayWritable writable = new ArrayWritable(Text.class);
        writable.set(new Text[] { new Text("cat"), new Text("dog") });

        TextArrayWritable dest = new TextArrayWritable();
        WritableUtils.cloneInto(dest, writable);
        assertThat(dest.get().length, is(2));
        assertThat((Text) dest.get()[0], is(new Text("cat")));
        assertThat((Text) dest.get()[1], is(new Text("dog")));

        Text[] copy = (Text[]) dest.toArray();
        assertThat(copy[0], is(new Text("cat")));
        assertThat(copy[1], is(new Text("dog")));
    }
}
