package lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Serializable;
import scala.Tuple2;

public class Main {

    public static class Values implements Serializable{
        private String delay;
        private String cancelled;
        private float maxDelay;
        private float percentsCancelled;
        private float percentsDelay;
        Values(String delay, String cancelled) {
            this.delay = delay;
            this.cancelled = cancelled;
        }

        Values(float maxDelay, float percentsDelay, float percentsCancelled) {
            this.maxDelay = maxDelay;
            this.percentsDelay = percentsDelay;
            this.percentsCancelled = percentsCancelled;
        }

        public String getDelay() {
            return this.delay;
        }


        public String getCancelled() {
            return this.cancelled;
        }

        public float getMaxDelay() {
            return this.maxDelay;
        }

        public float getPercentsCancelled() {
            return this.percentsCancelled;
        }

        public float getPercentsDelay() {
            return this.percentsDelay;
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
                                                    return new Tuple2<>(new Tuple2<>(originAirport, destAirport), new Values(delay, cancelled));
                                                }
                                            );
        JavaPairRDD<Tuple2<String, String>, Values> reducedData = data.groupByKey().mapValues(
                                                s -> {
                                                    float maxDelay = 0;
                                                    int countCancelled = 0, countDelay = 0, countFlights = 0;
                                                    for (Values val : s) {
                                                        countFlights++;
                                                        if (val.getCancelled() == "1.00") {
                                                            countCancelled++;
                                                            continue;
                                                        } else {
                                                            float delay = Float.parseFloat(val.getDelay());
                                                            if (delay > 0) {
                                                                countDelay++;
                                                                if (delay > maxDelay) maxDelay = delay;
                                                            }
                                                        }
                                                    }
                                                    float percentsDelay = countDelay * 100 / countFlights;
                                                    float percentsCancelled = countCancelled * 100 / countFlights;
                                                    return new Values(maxDelay, percentsDelay, percentsCancelled);
                                                }
                                        );

    }

}
