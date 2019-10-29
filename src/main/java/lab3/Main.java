package lab3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.Map;

public class Main {

    private static final String AIRPORTS_HEADER = "Code,Description";
    private static final String FLIGHTS_HEADER = "\"YEAR\",\"QUARTER\"";

    public static void main(String args[]){

        SparkConf conf = new SparkConf().setAppName("lab3");
        JavaSparkContext sc = new JavaSparkContext(conf);

        AirportsData airportsData = new AirportsData(sc, args[0], AIRPORTS_HEADER);
        JavaPairRDD<String, String> splitAirports = airportsData.makeSplit();
        final Broadcast<Map<String, String>> broadcastAirports = airportsData.makeBroadcast();

        FlightsData flightsData = new FlightsData(sc, args[1], FLIGHTS_HEADER);
        JavaPairRDD<AirportPair, Values> reducedFlights = flightsData.reduce();


        JavaRDD<String> output = reducedFlights.map((s) -> {
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
