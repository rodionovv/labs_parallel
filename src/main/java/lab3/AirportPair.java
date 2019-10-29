package lab3;

import java.io.Serializable;
import java.util.Objects;

public class AirportPair implements Serializable {


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
        return "Flight from " + originAirport + " to " + destAirport + ": ";
    }
}

