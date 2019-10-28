package lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Main {



    public static class AirportPair implements Serializable {
        private String originAirport;
        private String destAirport;
        AirportPair(String originAirport, String destAirport) {
            this.originAirport = originAirport;
            this.destAirport = destAirport;
        }

        public String getDestAirport() {
            return this.destAirport;
        }

        public String getOriginAirport() {
            return this.originAirport;
        }

        @Override
        public boolean equals(Object obj) {
            AirportPair p = (AirportPair) obj;
            if (this.originAirport.equals(p.originAirport)){
                if (this.destAirport.equals(p.destAirport)){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(originAirport, destAirport);
        }

        @Override
        public String toString() {
            return "Flight from " + originAirport + " to " + destAirport;
        }
    }


    public static class Values implements Serializable{
        private String delay;
        private String cancelled;
        private float maxDelay;
        private int countCanceled;
        private int countDelay;
        private int countFlights;


        Values(String delay, String cancelled) {
            this.delay = delay;
            this.cancelled = cancelled;
            this.countCanceled = 0;
            this.countDelay = 0;
            this.countFlights = 1;
        }

        public int getCountFlights() {
            return this.countFlights;
        }

        public String getDelay() {
            return this.delay;
        }

        public int getCountCanceled() {
            return this.countCanceled;
        }

        public int getCountDelay() {
            return this.countDelay;
        }


        public void setMaxDelay(float newMax) {
            this.maxDelay = newMax;
        }

        public void addCanceled(int val) {
            if (val == 0) {
                this.countCanceled++;
            } else {
                this.countCanceled += val;
            }
        }

        public void addFlights(int val) {
            this.countFlights += val;
        }


        public void addDelayed(int val) {
            if (val == 0) {
                this.countDelay++;
            } else {
                this.countDelay += val;
            }
        }

        public String getCancelled() {
            return this.cancelled;
        }

        public float getMaxDelay() {
            return this.maxDelay;
        }

        @Override
        public String toString() {
            if (this.countFlights != 0) {
                float percentsDelay = this.countDelay * 100 / this.countFlights;
                float percentsCancelled = this.countCanceled * 100 / this.countFlights;
                return "Max Delay = " + this.maxDelay + ", Delay Percentage = " + percentsDelay + "%, Canceled Percentage = " + percentsCancelled + "%";
            }
            return "delay = " + this.delay + ", cancelled = " + this.cancelled;
        }

    }


    public static void main(String args[]){

        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> airports = ParseCSV.readCSV(sc, args[0], "Code,Description");
        JavaRDD<String> flights = ParseCSV.readCSV(sc, args[1], "\"YEAR\",\"QUARTER\"");
        JavaPairRDD<String, String> splitterAirports = airports.mapToPair(
                                                        (s) -> {
                                                            String[] parts = ParseCSV.splitComma(s, 2);
                                                            String airportID = ParseCSV.getKey(parts);
                                                            String airportName = ParseCSV.getValue(parts);
                                                            return new Tuple2<>(airportID, airportName);
                                                        }
                                                    );

        Map<String, String> airportsMap = splitterAirports.collectAsMap();
        final Broadcast<Map<String, String>> broadcaastAirports = sc.broadcast(airportsMap);

        JavaPairRDD<AirportPair,Values> data = flights.mapToPair(
                                                s -> {
                                                        String[] parts = ParseCSV.splitComma(s);
                                                    String originAirport = ParseCSV.getKey(parts, 11);
                                                    String destAirport = ParseCSV.getKey(parts, 14);
                                                    String delay = ParseCSV.getValue(parts, 17);
                                                    String cancelled = ParseCSV.getValue(parts, 19);
                                                    return new Tuple2<>(new AirportPair(originAirport, destAirport), new Values(delay, cancelled));
                                                }
                                            );
        JavaPairRDD<AirportPair, Values> output = data.reduceByKey(
                (f, s) -> {
                    f.addFlights(s.getCountFlights());
                    if (s.getCancelled().equals("1.00")) {
                        f.addCanceled(s.getCountCanceled());
                    } else if (!s.getDelay().equals("")) {
                        float delay = Float.parseFloat(s.getDelay());
                        if (delay > 0) {
                            f.addDelayed(s.getCountDelay());
                            if (delay > f.getMaxDelay()) f.setMaxDelay(delay);
                        }
                    }
                    return f;
                }
        );

        output.map((s) -> {
                    String originAirportID = s._1.getOriginAirport();
                    String destAirportID = s._1.getDestAirport();
                    String originAirportName = broadcaastAirports.getValue().get(originAirportID);
                    String destAirportName = broadcaastAirports.getValue().get(destAirportID);
                    AirportPair pair = new AirportPair(originAirportName, destAirportName);
                    Values info = s._2;

                    return pair.toString() + info.toString();
                }
        );

        output.saveAsTextFile(args[2]);

    }

}
