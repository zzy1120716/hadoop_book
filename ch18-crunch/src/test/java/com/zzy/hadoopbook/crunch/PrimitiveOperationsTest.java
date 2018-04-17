package com.zzy.hadoopbook.crunch;

import static com.zzy.hadoopbook.crunch.PCollections.dump;
import static org.junit.Assert.assertEquals;
import static org.apache.crunch.types.writable.Writables.ints;
import static org.apache.crunch.types.writable.Writables.strings;
import static org.apache.crunch.types.writable.Writables.tableOf;

import org.apache.crunch.*;
import org.apache.crunch.impl.mem.MemPipeline;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.test.TemporaryPath;
import org.junit.Rule;
import org.junit.Test;

import java.io.Serializable;

public class PrimitiveOperationsTest implements Serializable {

    @Rule
    public transient TemporaryPath tmpDir = new TemporaryPath();

    @Test
    public void testPCollectionUnion() throws Exception {
        PCollection<Integer> a = MemPipeline.collectionOf(1, 3);
        PCollection<Integer> b = MemPipeline.collectionOf(2);
        PCollection<Integer> c = a.union(b);
        //assertEquals("{2,1,3}", dump(c));
        assertEquals("{1,3,2}", dump(c));
    }

    @Test
    public void testPCollectionParallelDo() throws Exception {
        PCollection<String> a = MemPipeline.collectionOf("cherry", "apple", "banana");
        PCollection<Integer> b = a.parallelDo(new DoFn<String, Integer>() {
            @Override
            public void process(String input, Emitter<Integer> emitter) {
                emitter.emit(input.length());
            }
        }, ints());
        assertEquals("{6,5,6}", dump(b));
    }

    @Test
    public void testPCollectionParallelDoMap() throws Exception {
        PCollection<String> a = MemPipeline.collectionOf("cherry", "apple", "banana");
        PCollection<Integer> b = a.parallelDo(new MapFn<String, Integer>() {
            @Override
            public Integer map(String input) {
                return input.length();
            }
        }, ints());
        assertEquals("{6,5,6}", dump(b));
    }

    @Test
    public void testPCollectionFilter() throws Exception {
        PCollection<String> a = MemPipeline.collectionOf("cherry", "apple", "banana");
        PCollection<String> b = a.filter(new FilterFn<String> () {
            @Override
            public boolean accept(String input) {
                return input.length() % 2 == 0;
            }
        });
        assertEquals("{cherry,banana}", dump(b));
    }

    @Test
    public void testPCollectionParallelDoExtractKey() throws Exception {
        PCollection<String> a = MemPipeline.collectionOf("cherry", "apple", "banana");
        PTable<Integer, String> b = a.parallelDo(new DoFn<String, Pair<Integer, String>>() {
            @Override
            public void process(String input, Emitter<Pair<Integer, String>> emitter) {
                emitter.emit(Pair.of(input.length(), input));
            }
        }, tableOf(ints(), strings()));
        assertEquals("{(6,cherry),(5,apple),(6,banana)}", dump(b));
    }

    @Test
    public void testGrouping() throws Exception {
        String inputPath = tmpDir.copyResourceFileName("fruit.txt");
        Pipeline pipeline = new MRPipeline(getClass());

        PCollection<String> a = pipeline.readTextFile(inputPath);
        assertEquals("{cherry,apple,banana}", dump(a));

        PTable<Integer, String> b = a.by(new MapFn<String, Integer>() {
            @Override
            public Integer map(String input) {
                return input.length();
            }
        }, ints());
        assertEquals("{(6,cherry),(5,apple),(6,banana)}", dump(b));
    }
}
