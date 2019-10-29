package lab3;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.Map;

public class AirportsData {

    private JavaSparkContext sc;
    private JavaRDD<String>  airports;
    private Broadcast<Map<String, String>> broadcastAirports;
    private JavaPairRDD<String, String> splittedAirports;

    AirportsData(JavaSparkContext sc, String path, String header) {
        this.sc = sc;
        this.airports = ParseCSV.readCSV(sc, path, header);
        this.broadcastAirports = null;
        this.splittedAirports = null;
    }


    public JavaPairRDD<String, String> makeSplit() {
        this.airports.mapToPair(
          s -> {
              String[] parts = ParseCSV.splitComma(s, 2);
              String airportID = ParseCSV.getKey(parts);
              String airportName = ParseCSV.getValue(parts);
              return new Tuple2<>(airportID, airportName);
          }
        );
    }

    public Broadcast<Map<String, String>> makeBroadcast(JavaSparkContext sc, JavaRDD<String> airports) {

    }

}
