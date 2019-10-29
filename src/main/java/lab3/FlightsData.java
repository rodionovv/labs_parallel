package lab3;


import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class FlightsData {

    private static final int ORIGIN_AIRPORT = 11;
    private static final int DEST_AIRPORT = 14;
    private static final int DELAY = 17;
    private static final int CANCELED = 19;



    private JavaRDD<String> flights;
    private JavaPairRDD<AirportPair, Values> splittedFlights;
    private JavaPairRDD<AirportPair, Values> reducedFlights;


    FlightsData(JavaSparkContext sc, String path, String header) {
        this.flights = ParseCSV.readCSV(sc, path, header);
        this.splittedFlights = null;
        this.reducedFlights = null;
    }

    public JavaPairRDD<AirportPair, Values> makeSplit() {
        this.splittedFlights = this.flights.mapToPair(
                s -> {
                    String[] parts = ParseCSV.splitComma(s);
                    String originAirport = ParseCSV.getKey(parts, ORIGIN_AIRPORT);
                    String destAirport = ParseCSV.getKey(parts, DEST_AIRPORT);
                    String delay = ParseCSV.getValue(parts, DELAY);
                    String cancelled = ParseCSV.getValue(parts, CANCELED);
                    return new Tuple2<>(new AirportPair(originAirport, destAirport), new Values(delay, cancelled));
                }
        );
        return this. splittedFlights;
    }

    public JavaPairRDD<AirportPair, Values> reduce() {
        if (splittedFlights == null) this.makeSplit();
        this.reducedFlights = this.splittedFlights.reduceByKey(
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
        return this.reducedFlights;
    }


}
