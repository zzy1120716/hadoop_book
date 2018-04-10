import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.StringUtils;

import java.io.*;

public class WritableTestBase {

    //结构化对象序列化为字节流
    public static byte[] serialize(Writable writable) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        writable.write(dataOut);
        dataOut.close();
        return out.toByteArray();
    }

    //字节流反序列化为结构化对象
    public static byte[] deserialize(Writable writable, byte[] bytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        DataInputStream dataIn = new DataInputStream(in);
        writable.readFields(dataIn);
        dataIn.close();
        return bytes;
    }

    //序列化为字符串
    public static String serializeToString(Writable src) throws IOException {
        return StringUtils.byteToHexString(serialize(src));
    }

    //反序列化为字符串
    public static String writeTo(Writable src, Writable dest) throws IOException {
        byte[] data = deserialize(dest, serialize(src));
        return StringUtils.byteToHexString(data);
    }
}
