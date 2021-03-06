package com.zzy.hadoopbook.crunch;

import com.zzy.hadoopbook.crunch.parser.NcdcRecordParser;
import com.zzy.hadoopbook.crunch.parser.NcdcStationMetadataParser;
import org.apache.crunch.*;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.lib.Join;

import static org.apache.crunch.types.writable.Writables.strings;
import static org.apache.crunch.types.writable.Writables.tableOf;

// Crunch version of ch09-mr-features JoinRecordWithStationName
public class JoinRecordWithStationNameCrunch {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: JoinRecordWithStationNameCrunch <ncdc input> <station input> <output>");
            System.exit(-1);
        }
        Pipeline pipeline = new MRPipeline(JoinRecordWithStationNameCrunch.class);
        PCollection<String> records = pipeline.readTextFile(args[0]);
        PCollection<String> stations = pipeline.readTextFile(args[1]);
        PTable<String, String> stationIdToRecord = records.parallelDo(toStationIdRecordPairsFn(), tableOf(strings(), strings()));
        PTable<String, String> stationIdToName = stations.parallelDo(toStationIdNamePairsFn(), tableOf(strings(), strings()));

        PTable<String, Pair<String, String>> joined = Join.join(stationIdToRecord, stationIdToName);

        pipeline.writeTextFile(joined, args[2]);
        pipeline.run();
    }

    private static DoFn<String, Pair<String, String>> toStationIdRecordPairsFn() {
        return new DoFn<String, Pair<String, String>>() {
            NcdcRecordParser parser = new NcdcRecordParser();
            @Override
            public void process(String input, Emitter<Pair<String, String>> emitter) {
                parser.parse(input);
                emitter.emit(Pair.of(parser.getStationId(), input));
            }
        };
    }

    private static DoFn<String, Pair<String, String>> toStationIdNamePairsFn() {
        return new DoFn<String, Pair<String, String>>() {
            NcdcStationMetadataParser parser = new NcdcStationMetadataParser();
            @Override
            public void process(String input, Emitter<Pair<String, String>> emitter) {
                parser.parse(input);
                emitter.emit(Pair.of(parser.getStationId(), input));
            }
        };
    }
}
