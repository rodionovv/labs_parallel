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
        private String delay;

        AirportPair(String originAirport, String destAirport,String delay){
            this.key = new Tuple2<>(originAirport, destAirport);
            this.delay = delay;
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

        public String getDelay(){
            return this.delay;
        }

    }


    public static void main(String args[]){

        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> airports = sc.textFile(args[0]);
        JavaRDD<String> flights = sc.textFile(args[1]);
        JavaPairRDD<String, String> splitterAirports = airports.mapToPair(
                                                        (s) -> {
                                                            if (s.startsWith("Code,Description")) {
                                                                return new Tuple2<>("", "");
                                                            }
                                                            String[] parts = ParseCSV.splitComma(s, 2);
                                                            String airportID = ParseCSV.getKey(parts);
                                                            String airportName = ParseCSV.getValue(parts);
                                                            return new Tuple2<>(airportID, airportName);
                                                        }
                                                    );
        JavaRDD<AirportPair> data = flights.map(
                                                s -> {
                                                    if (s.startsWith("\"YEAR\",\"QUARTER\"")){
                                                        return new AirportPair("", "", "");
                                                    }
                                                    String[] parts = ParseCSV.splitComma(s);
                                                    String originAirport = ParseCSV.getKey(parts, 11);
                                                    String destAirport = ParseCSV.getKey(parts, 14);
                                                    String delay = ParseCSV.getValue(parts, 17);
                                                    return new AirportPair(originAirport, destAirport, delay);
                                                }
                                            );
        

    }

}
