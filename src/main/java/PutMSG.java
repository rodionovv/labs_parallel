public class PutMSG {

    private String URL;
    private int count;
    private int time;

    public PutMSG(String URL, int count, int time) {
        this.URL = URL;
        this.count = count;
        this.time = time;
    }

    public String getURL() {
        return URL;
    }

    public int getCount() {
        return count;
    }

    public int getTime() {
        return time;
    }
}
