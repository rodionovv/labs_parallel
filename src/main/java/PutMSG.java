public class PutMSG {

    private String url;
    private int count;
    private int time;

    public PutMSG(String url, int count, int time) {
        this.url = url;
        this.count = count;
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public int getCount() {
        return count;
    }

    public int getTime() {
        return time;
    }
}
