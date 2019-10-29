package lab3;

import java.io.Serializable;

public static class Values implements Serializable {

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