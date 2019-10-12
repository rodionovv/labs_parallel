package org.rodionov;

import org.apache.hadoop.io.Text;

public class ParseCSV {

    static String[] splitComma(Text value) {
        return value.toString().split(",");
    }

    static String[] splitComma(Text value, int limit) {
        return value.toString().split(",", limit);
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
