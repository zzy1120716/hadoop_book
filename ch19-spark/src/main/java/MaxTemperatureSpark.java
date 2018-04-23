import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class MaxTemperatureSpark {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: MaxTemperatureSpark <input path> <output path>");
            System.exit(-1);
        }

        SparkConf conf = new SparkConf();
        JavaSparkContext sc = new JavaSparkContext("local", "MaxTemperatureSpark", conf);
        JavaRDD<String> lines = sc.textFile(args[0]);
        JavaRDD<String[]> records = lines.map(s -> s.split("\t"));
        /*JavaRDD<String[]> records = lines.map(new Function<String, String[]>() {
            @Override
            public String[] call(String s) throws Exception {
                return s.split("\t");
            }
        });*/
        JavaRDD<String[]> filtered = records.filter(rec -> !rec[1].equals("9999") && rec[2].matches("[01459]"));
        /*JavaRDD<String[]> filtered = records.filter(new Function<String[], Boolean>() {
            @Override
            public Boolean call(String[] rec) throws Exception {
                return rec[1] != "9999" && rec[2].matches("[01459]");
            }
        });*/
        JavaPairRDD<Integer, Integer> tuples = filtered.mapToPair(rec -> new Tuple2<Integer, Integer>(Integer.parseInt(rec[0]), Integer.parseInt(rec[1])));
        /*JavaPairRDD<Integer, Integer> tuples = filtered.mapToPair(new PairFunction<String[], Integer, Integer>() {
            @Override
            public Tuple2<Integer, Integer> call(String[] rec) throws Exception {
                return new Tuple2<Integer, Integer>(
                        Integer.parseInt(rec[0]), Integer.parseInt(rec[1])
                );
            }
        });*/
        JavaPairRDD<Integer, Integer> maxTemps = tuples.reduceByKey(Math::max);
        /*JavaPairRDD<Integer, Integer> maxTemps = tuples.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer i1, Integer i2) throws Exception {
                return Math.max(i1, i2);
            }
        });*/
        maxTemps.saveAsTextFile(args[1]);
    }
}
