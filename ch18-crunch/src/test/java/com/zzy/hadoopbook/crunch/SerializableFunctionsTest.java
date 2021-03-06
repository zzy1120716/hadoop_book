package com.zzy.hadoopbook.crunch;

import com.zzy.hadoopbook.crunch.fn.CustomDoFn;
import org.apache.crunch.PCollection;
import org.apache.crunch.Pipeline;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.test.TemporaryPath;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import static org.apache.crunch.types.writable.Writables.strings;
import static org.junit.Assert.assertEquals;

public class SerializableFunctionsTest {

    @Rule
    public transient TemporaryPath tmpDir = new TemporaryPath();

    @Test
    public void testInitialize() throws IOException {
        String inputPath = tmpDir.copyResourceFileName("set1.txt");
        Pipeline pipeline = new MRPipeline(getClass());
        PCollection<String> lines = pipeline.readTextFile(inputPath);
        long len = lines.parallelDo(new CustomDoFn<String, String>(), strings()).length().getValue();
        assertEquals(4, len);
        pipeline.done();
    }
}
