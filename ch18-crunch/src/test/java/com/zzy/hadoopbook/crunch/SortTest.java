package com.zzy.hadoopbook.crunch;

import org.apache.crunch.PTable;
import org.apache.crunch.impl.mem.MemPipeline;
import org.apache.crunch.lib.Sort;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.apache.crunch.types.writable.Writables.ints;
import static org.apache.crunch.types.writable.Writables.tableOf;
import static org.junit.Assert.assertEquals;
import static com.zzy.hadoopbook.crunch.tools.PCollections.dump;

public class SortTest {

    private PTable<Integer, Integer> a;

    @Before
    public void setup() {
        a = MemPipeline.typedTableOf(tableOf(ints(), ints()), 2, 3, 1, 2, 2, 4);
    }

    @Test
    public void testSortTableByKey() throws IOException {
        assertEquals("{(2,3),(1,2),(2,4)}", dump(a));
        PTable<Integer, Integer> b = Sort.sort(a);
        assertEquals("{(1,2),(2,3),(2,4)}", dump(b));
    }

    // 使用sortPairs当做pairs，能够对值施加一个顺序

    // 如何对Avro对象进行排序？ 需要使用特定的命令字段来生成它，如Avro章节中所讨论的。
}
