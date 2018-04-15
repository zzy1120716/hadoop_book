package com.zzy.hadoopbook.crunch;

import static org.apache.crunch.types.writable.Writables.strings;

import org.apache.crunch.*;

import java.util.Iterator;

public class PCollections {

    public static <S> String dump(PCollection<S> collection) {
        StringBuilder sb = new StringBuilder("{");
        for (Iterator<S> i = collection.materialize().iterator(); i.hasNext(); ) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public static <K, V> String dump(PTable<K, V> table) {
        StringBuilder sb = new StringBuilder("{");
        for (Iterator<Pair<K, V>> i = table.materialize().iterator(); i.hasNext(); ) {
            Pair<K, V> pair = i.next();
            sb.append("(").append(pair.first()).append(",").append(pair.second()).append(")");
            if (i.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public static <K, V> String dump(PGroupedTable<K, V> groupedTable) {
        return dump(groupedTable.mapValues(new MapFn<Iterable<V>, String>() {
            @Override
            public String map(Iterable<V> input) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (Iterator<V> i = input.iterator(); i.hasNext(); ) {
                    sb.append(i.next());
                    if (i.hasNext()) {
                        sb.append(",");
                    }
                }
                sb.append("]");
                return sb.toString();
            }
        }, strings()));
    }
}
