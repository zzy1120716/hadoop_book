import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.net.URI;

public class MapFileWriteDemo {

    private static final String[] DATA = {
            "汉皇重色思倾国，御宇多年求不得。",
            "杨家有女初长成，养在深闺人未识。",
            "天生丽质难自弃，一朝选在君王侧。",
            "回眸一笑百媚生，六宫粉黛无颜色。",
            "春寒赐浴华清池，温泉水滑洗凝脂。",
            "侍儿扶起娇无力，始是新承恩泽时。",
            "云鬓花颜金步摇，芙蓉帐暖度春宵。",
            "春宵苦短日高起，从此君王不早朝。"
    };

    public static void main(String[] args) throws IOException {
        String uri = args[0];
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), conf);

        IntWritable key = new IntWritable();
        Text value = new Text();
        MapFile.Writer writer = null;
        try {
            writer = new MapFile.Writer(conf, fs, uri, key.getClass(), value.getClass());

            for(int i = 0; i < 1024; i++) {
                key.set(i + 1);
                value.set(DATA[i % DATA.length]);
                writer.append(key, value);
            }
        } finally {
            IOUtils.closeStream(writer);
        }
    }
}
