package com.zzy.hadoopbook.crunch;

import com.zzy.hadoopbook.crunch.parser.NcdcRecordParser;
import org.apache.crunch.*;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.lib.Sort;

import static org.apache.crunch.types.writable.Writables.ints;
import static org.apache.crunch.types.writable.Writables.pairs;
import static org.apache.crunch.lib.Sort.ColumnOrder.by;

// Crunch version of ch09-mr-features MaxTemperatureUsingSecondarySort
public class MaxTemperatureUsingSecondarySortCrunch {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: MaxTemperatureUsingSecondarySortCrunch <input path> <output path>");
            System.exit(-1);
        }
        Pipeline pipeline = new MRPipeline(MaxTemperatureUsingSecondarySortCrunch.class);
        PCollection<String> records = pipeline.readTextFile(args[0]);
        PCollection<Pair<Integer, Integer>> pairs = records
                .parallelDo(toYearTempValueFn(), pairs(ints(), ints()));

        PCollection<Pair<Integer, Integer>> sorted =
                Sort.sortPairs(pairs, by(1, Sort.Order.ASCENDING), by(2, Sort.Order.DESCENDING));
        pipeline.writeTextFile(sorted, args[1]);
        pipeline.run();
    }

    private static DoFn<String, Pair<Integer, Integer>> toYearTempValueFn() {
        return new DoFn<String, Pair<Integer, Integer>>() {
            NcdcRecordParser parser = new NcdcRecordParser();
            @Override
            public void process(String input, Emitter<Pair<Integer, Integer>> emitter) {
                parser.parse(input);
                if (parser.isValidTemperature()) {
                    emitter.emit(Pair.of(parser.getYearInt(), parser.getAirTemperature()));
                }
            }
        };
    }
}
