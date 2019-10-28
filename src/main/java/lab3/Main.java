package lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class Main {

    public static void main(String args[]){
        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> airports = sc.textFile(args[0]);
        JavaRDD<String> delay = sc.textFile(args[1]);
        JavaPairRDD<String, String> splitterAirports = airports.mapToPair(
                                                        (s) -> {
                                                            if (s.startsWith("Code,Description")) {
                                                                return new Tuple2<>("", "");
                                                            }
                                                            String[] parts = ParseCSV.splitComma(s, 2);
                                                            String airprotID = ParseCSV.getKey(parts);
                                                            String airportName = ParseCSV.getValue(parts);
                                                            return new Tuple2<>(airprotID, airportName);
                                                        }
                                                    );
    }

}
