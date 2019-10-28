package lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Serializable;
import scala.Tuple2;

public class Main {

    public static class AirportPair implements Serializable {
        private Tuple2<String, String> key;

        AirportPair(String originAirport, String destAirport){
            this.key = new Tuple2<>(originAirport, destAirport);
        }

        public Tuple2 getPair(){
            return this.key;
        }

        public String getOriginAirport(){
            return this.key._1;
        }

        public String getDesAirport(){
            return this.key._2;
        }

    }

    public static class Values implements Serializable{
        private String delay;
        private String cancelled;
        Values(String delay, String cancelled) {
            this.delay = delay;
            this.cancelled = cancelled;
        }

        public String getDelay() {
            return this.delay;
        }


        public String getCancelled() {
            return this.cancelled;
        }
    }


    public static void main(String args[]){

        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> airports = sc.textFile(args[0]);
        airports.filter(
                s -> !s.startsWith("Code,Description")
        );
        JavaRDD<String> flights = sc.textFile(args[1]);
        flights.filter(
                s -> !s.startsWith("\"YEAR\",\"QUARTER\"")
        );
        JavaPairRDD<String, String> splitterAirports = airports.mapToPair(
                                                        (s) -> {
                                                            String[] parts = ParseCSV.splitComma(s, 2);
                                                            String airportID = ParseCSV.getKey(parts);
                                                            String airportName = ParseCSV.getValue(parts);
                                                            return new Tuple2<>(airportID, airportName);
                                                        }
                                                    );
        JavaPairRDD<Tuple2<String, String>,Values> data = flights.mapToPair(
                                                s -> {
                                                    String[] parts = ParseCSV.splitComma(s);
                                                    String originAirport = ParseCSV.getKey(parts, 11);
                                                    String destAirport = ParseCSV.getKey(parts, 14);
                                                    String delay = ParseCSV.getValue(parts, 17);
                                                    String cancelled = ParseCSV.getValue(parts, 19);
                                                    return new Tuple2<>(new Tuple2<>(originAirport, destAirport), new Values(delay, cancelled);
                                                }
                                            );
        JavaPairRDD<Tuple2<String, String>, Values> reducedData = data.reduceByKey(
                (f, s) -> {

                }
        );
    }

}
