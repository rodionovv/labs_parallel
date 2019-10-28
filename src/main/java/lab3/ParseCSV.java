package lab3;

import org.apache.hadoop.io.Text;

public class ParseCSV {

    static String[] splitComma(String value) {
        return value.split(",");
    }

    static String[] splitComma(String value, int limit) {
        return value.split(",", limit);
    }

    static String getKey(String[] parts){
        return parts[0].split("\"")[1];
    }

    static String getValue(String[] parts) {
        return parts[1].split("\"")[1].split("\"")[0];
    }

    static String getKey(String[] parts, int i){
        return parts[i];
    }

    static String getValue(String[] parts, int i) {
        return parts[i];
    }

}