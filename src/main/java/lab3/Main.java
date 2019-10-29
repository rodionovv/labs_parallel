package lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.Map;

public class Main {

    public static void main(String args[]){

        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);
        AirportsData airportsData = new AirportsData(sc, args[0], "Code,Description");
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
        final Broadcast<Map<String, String>> broadcastAirports = makeBroadCast(sc, airports);

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
        JavaPairRDD<AirportPair, Values> reducedData = data.reduceByKey(
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

        JavaRDD<String> output = reducedData.map((s) -> {
                    String originAirportID = s._1.getOriginAirport();
                    String destAirportID = s._1.getDestAirport();
                    String originAirportName = broadcastAirports.getValue().get(originAirportID);
                    String destAirportName = broadcastAirports.getValue().get(destAirportID);
                    AirportPair pair = new AirportPair(originAirportName, destAirportName);
                    Values info = s._2;
                    return pair.toString() + info.toString();
                }
        );

        output.saveAsTextFile(args[2]);

    }

}
