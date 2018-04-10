import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import java.io.IOException;

public class SingleResourceConfigurationTest {

    @Test
    public void get() throws IOException {
        Configuration conf = new Configuration();
        conf.addResource("configuration-1.xml");
        assertThat(conf.get("color"), is("yellow"));
        assertThat(conf.getInt("size", 0), is(10));
        assertThat(conf.get("breadth", "wide"), is("wide"));    //未定义的属性，传入默认值
    }
}
