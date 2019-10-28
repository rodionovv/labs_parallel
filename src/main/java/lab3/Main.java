package lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Serializable;
import scala.Tuple2;

public class Main {



    public static class AirportPair implements Serializable{
        private Tuple2<String, String> pair;
        AirportPair(String originAirport, String destAirport) {
            this.pair = new Tuple2<>(originAirport, destAirport);
        }

        @Override
        public boolean equals(Object obj) {
            AirportPair p = (AirportPair) obj;
            return this.pair._1 == p.pair._1 && this.pair._2 == p.pair._2;
        }
    }


    public static class Values{
        private String delay;
        private String cancelled;
        private float maxDelay;
//        private float percentsCancelled;
//        private float percentsDelay;
        private int countCanceled;
        private int countDelay;
        private int countFlights;
        Values(String delay, String cancelled) {
            this.delay = delay;
            this.cancelled = cancelled;
            this.countCanceled = 0;
            this.countDelay = 0;
            this.countFlights = 0;
        }



//        Values(float maxDelay, float percentsDelay, float percentsCancelled) {
//            this.maxDelay = maxDelay;
//            this.percentsDelay = percentsDelay;
//            this.percentsCancelled = percentsCancelled;
//        }

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

        @Override
        public String toString() {
            float percentsDelay = this.countDelay * 100 / countFlights;
            float percentsCancelled = this.countCanceled * 100 / countFlights;
            return "Max Delay = " + this.maxDelay + ", Delay Percentage = " + percentsDelay + "%, Canceled Percentage = " + percentsCancelled + "%";
        }

        public void setMaxDelay(float newMax) {
            this.maxDelay = newMax;
        }

        public void addCanceled(int val) {
            this.countCanceled += val;
        }

        public void addFlights(int val) {
            this.countFlights += val;
        }


        public void addDelayed(int val) {
            this.countDelay += val;
        }

        public String getCancelled() {
            return this.cancelled;
        }

        public float getMaxDelay() {
            return this.maxDelay;
        }

//        public float getPercentsCancelled() {
//            return this.percentsCancelled;
//        }
//
//        public float getPercentsDelay() {
//            return this.percentsDelay;
//        }
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
                    } else {
                        float delay = Float.parseFloat(s.getDelay());
                        if (delay > 0) {
                            f.addDelayed(s.getCountDelay());
                            if (delay > f.getMaxDelay()) f.setMaxDelay(delay);
                        }
                    }
                    return f;
                }
        );
//        data.groupByKey();
        output.saveAsTextFile(args[2]);
////                .mapValues(
//                        s -> {
//                            float maxDelay = 0;
//                            int countCancelled = 0, countDelay = 0, countFlights = 0;
//                            for (Values val : s) {
//                                countFlights++;
//                                if (val.getCancelled() == "1.00") {
//                                    countCancelled++;
//                                    continue;
//                                } else {
//                                    float delay = Float.parseFloat(val.getDelay());
//                                    if (delay > 0) {
//                                        countDelay++;
//                                        if (delay > maxDelay) maxDelay = delay;
//                                    }
//                                }
//                            }
//                            float percentsDelay = countDelay * 100 / countFlights;
//                            float percentsCancelled = countCancelled * 100 / countFlights;
//                            return new Values(maxDelay, percentsDelay, percentsCancelled);
//                        }
//                );

    }

}
