package lab3;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class FlightsData {

    private JavaRDD<String> flights;
    private JavaPairRDD<AirportPair, Values> splittedFlights;
    private JavaPairRDD<AirportPair, Values> reducedFlights;


    FlightsData(JavaSparkContext sc, String path, String header) {
        this.flights = ParseCSV.readCSV(sc, path, header);
        this.splittedFlights = null;
        this.reducedFlights = null;
    }

    public void makeSplit() {
        this.splittedFlights = this.flights.mapToPair(
                s -> {
                    String[] parts = ParseCSV.splitComma(s);
                    String originAirport = ParseCSV.getKey(parts, 11);
                    String destAirport = ParseCSV.getKey(parts, 14);
                    String delay = ParseCSV.getValue(parts, 17);
                    String cancelled = ParseCSV.getValue(parts, 19);
                    return new Tuple2<>(new AirportPair(originAirport, destAirport), new Values(delay, cancelled));
                }
        );
    }


}
