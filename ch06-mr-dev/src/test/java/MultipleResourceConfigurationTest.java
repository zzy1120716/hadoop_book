import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import java.io.IOException;

public class MultipleResourceConfigurationTest {

    @Test
    public void get() throws IOException {
        Configuration conf = new Configuration();
        conf.addResource("configuration-1.xml");
        conf.addResource("configuration-2.xml");

        assertThat(conf.get("color"), is("yellow"));

        // override
        assertThat(conf.getInt("size", 0), is(12));

        // final properties cannot be overridden
        assertThat(conf.get("weight"), is("heavy"));

        // variable expansion
        assertThat(conf.get("size-weight"), is("12,heavy"));

        // variable expansion with system properties
        System.setProperty("size", "14");
        assertThat(conf.get("size-weight"), is("14,heavy"));

        //system properties are not picked up
        System.setProperty("length", "2");
        assertThat(conf.get("length"), is((String) null));  //没有设置的属性，get不到
    }
}
